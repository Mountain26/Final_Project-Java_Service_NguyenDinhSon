package ra.edu.finalproject_javaservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.CourseResponse;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.service.EnrollmentService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock EnrollmentService enrollmentService;
    @Mock SubmissionService submissionService;
    @InjectMocks StudentController studentController;

    @Test
    void myCoursesReturnsServiceData() {
        CourseResponse course = new CourseResponse(1L, "JAVA101", "Java Basics", 3);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("student", "N/A"));
        when(enrollmentService.findMyCourses("student")).thenReturn(List.of(course));

        ApiResponse<java.util.List<CourseResponse>> result = studentController.myCourses();

        assertEquals("Success", result.message());
        assertEquals(List.of(course), result.data());
    }

    @Test
    void uploadDelegatesToService() {
        SubmissionResponse response = new SubmissionResponse(1L, 10L, 20L, "url", null, null, "SUBMITTED");
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "content".getBytes());
        when(submissionService.upload(1L, file)).thenReturn(response);

        ApiResponse<SubmissionResponse> result = studentController.upload(1L, file);

        assertEquals("Uploaded successfully", result.message());
        assertEquals(response, result.data());
    }
}
