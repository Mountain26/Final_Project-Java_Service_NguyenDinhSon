package ra.edu.finalproject_javaservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.dto.EnrollCourseRequest;
import ra.edu.finalproject_javaservice.entity.Course;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.repository.CourseRepository;
import ra.edu.finalproject_javaservice.repository.EnrollmentRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock CourseRepository courseRepository;
    @Mock UserRepository userRepository;
    @InjectMocks EnrollmentService enrollmentService;

    @Test
    void enrollStudentSuccess() {
        User student = new User();
        student.setId(1L); student.setUsername("student"); student.setRole(Role.STUDENT);
        Course course = new Course(); course.setId(2L);
        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudent_IdAndCourse_Id(1L, 2L)).thenReturn(false);
        enrollmentService.enroll("student", new EnrollCourseRequest(2L));
    }
}
