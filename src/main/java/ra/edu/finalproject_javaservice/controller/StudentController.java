package ra.edu.finalproject_javaservice.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.EnrollCourseRequest;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.service.EnrollmentService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {
    private final EnrollmentService enrollmentService;
    private final SubmissionService submissionService;
    public StudentController(EnrollmentService enrollmentService, SubmissionService submissionService) {
        this.enrollmentService = enrollmentService;
        this.submissionService = submissionService;
    }

    @PostMapping("/courses/enroll")
    public ApiResponse<Void> enroll(@Valid @RequestBody EnrollCourseRequest request) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        enrollmentService.enroll(username, request);
        return ApiResponse.ok("Enrolled successfully", null);
    }

    @PostMapping(value = "/submissions/{submissionId}/upload", consumes = "multipart/form-data")
    public ApiResponse<SubmissionResponse> upload(@PathVariable Long submissionId, @RequestPart MultipartFile file) {
        return ApiResponse.ok("Uploaded successfully", submissionService.upload(submissionId, file));
    }

    @PostMapping(value = "/courses/{courseId}/submissions/upload", consumes = "multipart/form-data")
    public ApiResponse<SubmissionResponse> uploadForCourse(@PathVariable Long courseId, @RequestPart MultipartFile file) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.ok("Uploaded successfully", submissionService.uploadForStudent(username, courseId, file));
    }
    @GetMapping("/submissions/me")
    public ApiResponse<?> mySubmissions() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.ok("Success", submissionService.findByUsername(username));
    }
}
