package ra.edu.finalproject_javaservice.service;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.entity.*;
import ra.edu.finalproject_javaservice.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    @Mock SubmissionRepository submissionRepository;
    @Mock CourseRepository courseRepository;
    @Mock UserRepository userRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock Cloudinary cloudinary;
    @InjectMocks SubmissionService submissionService;

    @Test
    void findByUsernameReturnsSubmissions() {
        User student = new User();
        student.setId(1L); student.setUsername("student");
        Submission submission = new Submission();
        submission.setId(1L); submission.setStudent(student); submission.setCourse(new Course());
        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(submissionRepository.findByStudent_Id(1L)).thenReturn(List.of(submission));
        assertEquals(1, submissionService.findByUsername("student").size());
    }
}
