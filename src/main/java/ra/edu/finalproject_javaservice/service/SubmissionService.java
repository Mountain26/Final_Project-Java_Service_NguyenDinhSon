package ra.edu.finalproject_javaservice.service;

import com.cloudinary.Cloudinary;
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
import java.util.Map;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final Cloudinary cloudinary;
    public SubmissionService(SubmissionRepository submissionRepository, CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository, Cloudinary cloudinary) {
        this.submissionRepository = submissionRepository; this.courseRepository = courseRepository; this.userRepository = userRepository; this.enrollmentRepository = enrollmentRepository; this.cloudinary = cloudinary;
    }
    public SubmissionResponse create(CreateSubmissionRequest request) {
        Submission s = new Submission();
        s.setCourse(courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found")));
        s.setStudent(userRepository.findById(request.studentId()).orElseThrow(() -> new NotFoundException("Student not found")));
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(request.studentId(), request.courseId())) {
            throw new BadRequestException("Student is not enrolled in this course");
        }
        s.setReportUrl(null);
        s.setStatus(SubmissionStatus.PENDING);
        submissionRepository.save(s);
        return toResponse(s);
    }

    public SubmissionResponse uploadForStudent(String username, Long courseId, MultipartFile file) {
        var student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), courseId)) {
            throw new BadRequestException("Student is not enrolled in this course");
        }
        Submission submission = submissionRepository.findByStudent_Id(student.getId()).stream()
                .filter(s -> s.getCourse().getId().equals(courseId) && s.getStatus() != SubmissionStatus.GRADED)
                .findFirst()
                .orElseGet(() -> {
                    Submission s = new Submission();
                    s.setStudent(student);
                    s.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found")));
                    s.setStatus(SubmissionStatus.PENDING);
                    return submissionRepository.save(s);
                });
        return upload(submission.getId(), file);
    }
    public SubmissionResponse grade(GradeRequest request) {
        Submission s = submissionRepository.findById(request.submissionId()).orElseThrow(() -> new NotFoundException("Submission not found"));
        if (s.getStatus() != SubmissionStatus.SUBMITTED && s.getStatus() != SubmissionStatus.LATE) throw new BadRequestException("Submission is not ready for grading");
        s.setScore(request.score());
        s.setFeedback(request.feedback());
        s.setStatus(SubmissionStatus.GRADED);
        submissionRepository.save(s);
        return toResponse(s);
    }
    public java.util.List<SubmissionResponse> findByCourse(Long courseId) {
        return submissionRepository.findByCourse_Id(courseId).stream().map(this::toResponse).toList();
    }
    public java.util.List<SubmissionResponse> findByStudent(Long studentId) {
        return submissionRepository.findByStudent_Id(studentId).stream().map(this::toResponse).toList();
    }
    public java.util.List<SubmissionResponse> findByUsername(String username) {
        var student = userRepository.findByUsername(username).orElseThrow();
        return findByStudent(student.getId());
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
        s.setStatus(SubmissionStatus.SUBMITTED);
        submissionRepository.save(s);
        return toResponse(s);
    }
    private SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(s.getId(), s.getCourse().getId(), s.getStudent().getId(), s.getReportUrl(), s.getScore(), s.getFeedback(), s.getStatus().name());
    }
}
