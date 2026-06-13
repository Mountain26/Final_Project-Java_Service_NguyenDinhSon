package ra.edu.finalproject_javaservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.finalproject_javaservice.dto.AssignmentResponse;
import ra.edu.finalproject_javaservice.dto.CreateAssignmentRequest;
import ra.edu.finalproject_javaservice.entity.Assignment;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.exception.BadRequestException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.AssignmentRepository;
import ra.edu.finalproject_javaservice.repository.CourseRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public AssignmentResponse create(String lecturerUsername, CreateAssignmentRequest request) {
        var course = courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found"));
        User lecturer = userRepository.findByUsername(lecturerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        if (lecturer.getRole() == null || !lecturer.getRole().name().equals("LECTURER")) {
            throw new BadRequestException("Only lecturer can create assignment");
        }
        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setLecturer(lecturer);
        assignment.setTitle(request.title().trim());
        assignment.setDescription(request.description());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxScore(request.maxScore());
        assignmentRepository.save(assignment);
        return toResponse(assignment);
    }

    public Page<AssignmentResponse> findByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AssignmentResponse> result = assignmentRepository.findByCourse_Id(courseId, pageable).map(this::toResponse);
        if (result.isEmpty()) {
            throw new NotFoundException("No assignments found for this course");
        }
        return result;
    }

    public AssignmentResponse findOne(Long id) {
        return toResponse(assignmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Assignment not found")));
    }

    private AssignmentResponse toResponse(Assignment a) {
        return new AssignmentResponse(
                a.getId(),
                a.getCourse().getId(),
                a.getLecturer().getId(),
                a.getTitle(),
                a.getDescription(),
                a.getDueDate(),
                a.getMaxScore()
        );
    }
}
