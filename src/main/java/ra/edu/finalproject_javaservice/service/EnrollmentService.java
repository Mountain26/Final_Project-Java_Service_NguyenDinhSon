package ra.edu.finalproject_javaservice.service;

import org.springframework.stereotype.Service;
import ra.edu.finalproject_javaservice.dto.EnrollCourseRequest;
import ra.edu.finalproject_javaservice.entity.Enrollment;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.ForbiddenException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.CourseRepository;
import ra.edu.finalproject_javaservice.repository.EnrollmentRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public void enroll(String username, EnrollCourseRequest request) {
        var student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        if (student.getRole() != Role.STUDENT) throw new ForbiddenException("Only student can enroll");
        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), request.courseId())) {
            throw new ConflictException("Student already enrolled in this course");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found")));
        enrollmentRepository.save(enrollment);
    }
}
