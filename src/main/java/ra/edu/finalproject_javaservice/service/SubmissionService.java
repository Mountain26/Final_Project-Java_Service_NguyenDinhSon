package ra.edu.finalproject_javaservice.service;

import com.cloudinary.Cloudinary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.finalproject_javaservice.dto.CreateSubmissionRequest;
import ra.edu.finalproject_javaservice.dto.GradeRequest;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.entity.*;
import ra.edu.finalproject_javaservice.exception.BadRequestException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final Cloudinary cloudinary;
    public SubmissionService(SubmissionRepository submissionRepository, CourseRepository courseRepository, AssignmentRepository assignmentRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository, Cloudinary cloudinary) {
        this.submissionRepository = submissionRepository; this.courseRepository = courseRepository; this.assignmentRepository = assignmentRepository; this.userRepository = userRepository; this.enrollmentRepository = enrollmentRepository; this.cloudinary = cloudinary;
    }
    public SubmissionResponse create(CreateSubmissionRequest request) {
        Submission s = new Submission();
        s.setCourse(courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found")));
        s.setStudent(userRepository.findById(request.studentId()).orElseThrow(() -> new NotFoundException("Student not found")));
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(request.studentId(), request.courseId())) {
            throw new BadRequestException("Student has not registered for this course");
        }
        s.setReportUrl(null);
        s.setStatus(SubmissionStatus.PENDING);
        submissionRepository.save(s);
        return toResponse(s);
    }

    public SubmissionResponse uploadForStudent(String username, Long courseId, MultipartFile file) {
        var student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), courseId)) {
            throw new BadRequestException("You have not registered for this course, so you cannot submit an assignment here");
        }
        Submission submission = submissionRepository.findByStudent_IdAndCourse_IdAndStatusNot(student.getId(), courseId, SubmissionStatus.GRADED)
                .orElseGet(() -> {
                    Submission s = new Submission();
                    s.setStudent(student);
                    s.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found")));
                    s.setStatus(SubmissionStatus.PENDING);
                    return submissionRepository.save(s);
        });
        return upload(submission.getId(), file);
    }

    public SubmissionResponse uploadForAssignment(String username, Long assignmentId, MultipartFile file) {
        var student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new NotFoundException("Assignment not found"));
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), assignment.getCourse().getId())) {
            throw new BadRequestException("You have not registered for this course, so you cannot submit this assignment");
        }
        Submission submission = submissionRepository.findByStudent_IdAndCourse_IdAndStatusNot(student.getId(), assignment.getCourse().getId(), SubmissionStatus.GRADED)
                .orElseGet(() -> {
                    Submission s = new Submission();
                    s.setStudent(student);
                    s.setCourse(assignment.getCourse());
                    s.setAssignment(assignment);
                    s.setStatus(SubmissionStatus.PENDING);
                    return submissionRepository.save(s);
                });
        if (submission.getAssignment() == null) {
            submission.setAssignment(assignment);
            submissionRepository.save(submission);
        }
        return upload(submission.getId(), file);
    }
    public SubmissionResponse grade(GradeRequest request) {
        Submission s = submissionRepository.findById(request.submissionId()).orElseThrow(() -> new NotFoundException("Submission not found"));
        if (s.getStatus() != SubmissionStatus.SUBMITTED && s.getStatus() != SubmissionStatus.LATE) throw new BadRequestException("Submission is not ready for grading");
        if (s.getAssignment() != null && request.score() > s.getAssignment().getMaxScore()) {
            throw new BadRequestException("Score cannot exceed assignment maximum score of " + s.getAssignment().getMaxScore());
        }
        s.setScore(request.score());
        s.setFeedback(request.feedback());
        s.setStatus(SubmissionStatus.GRADED);
        submissionRepository.save(s);
        return toResponse(s);
    }
    public Page<SubmissionResponse> findByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return submissionRepository.findByCourse_Id(courseId, pageable).map(this::toResponse);
    }
    public Page<SubmissionResponse> findByStudent(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return submissionRepository.findByStudent_Id(studentId, pageable).map(this::toResponse);
    }
    public Page<SubmissionResponse> findByUsername(String username, int page, int size) {
        var student = userRepository.findByUsername(username).orElseThrow();
        return findByStudent(student.getId(), page, size);
    }
    public void delete(Long id) { submissionRepository.deleteById(id); }
    public SubmissionResponse upload(Long submissionId, MultipartFile file) {
        Submission s = submissionRepository.findById(submissionId).orElseThrow(() -> new NotFoundException("Submission not found"));
        try {
            Map<?, ?> upload = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "auto",
                    "folder", "finalproject/submissions"
            ));
            s.setReportUrl(upload.get("secure_url").toString());
        } catch (IOException e) {
            throw new RuntimeException("Cloud upload failed");
        }
        if (s.getAssignment() != null && LocalDateTime.now().isAfter(s.getAssignment().getDueDate())) {
            s.setStatus(SubmissionStatus.LATE);
        } else {
            s.setStatus(SubmissionStatus.SUBMITTED);
        }
        submissionRepository.save(s);
        return toResponse(s);
    }
    private SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(s.getId(), s.getCourse().getId(), s.getStudent().getId(), s.getReportUrl(), s.getScore(), s.getFeedback(), s.getStatus().name());
    }
}
