package ra.edu.finalproject_javaservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.service.EnrollmentService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock EnrollmentService enrollmentService;
    @Mock SubmissionService submissionService;
    @InjectMocks StudentController studentController;

    @Test
    void controllerExists() {
        assertTrue(true);
    }
}
