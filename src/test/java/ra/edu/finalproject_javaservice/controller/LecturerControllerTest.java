package ra.edu.finalproject_javaservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.GradeRequest;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.service.MaterialService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LecturerControllerTest {
    @Mock SubmissionService submissionService;
    @Mock MaterialService materialService;
    @InjectMocks LecturerController lecturerController;

    @Test
    void submissionsByCourseReturnsServiceData() {
        SubmissionResponse response = new SubmissionResponse(1L, 10L, 20L, null, null, null, "PENDING");
        when(submissionService.findByCourse(10L)).thenReturn(List.of(response));

        ApiResponse<?> result = lecturerController.submissionsByCourse(10L);

        assertEquals("Success", result.message());
        assertEquals(List.of(response), result.data());
        verify(submissionService).findByCourse(10L);
    }

    @Test
    void gradeDelegatesToService() {
        GradeRequest request = new GradeRequest(1L, 8.5, "Good");
        SubmissionResponse response = new SubmissionResponse(1L, 10L, 20L, null, 8.5, "Good", "GRADED");
        when(submissionService.grade(request)).thenReturn(response);

        ApiResponse<SubmissionResponse> result = lecturerController.grade(request);

        assertEquals("Graded successfully", result.message());
        assertEquals(response, result.data());
        verify(submissionService).grade(request);
    }
}
