package ra.edu.finalproject_javaservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.AssignmentResponse;
import ra.edu.finalproject_javaservice.dto.CreateAssignmentRequest;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.service.AssignmentService;
import ra.edu.finalproject_javaservice.service.EnrollmentService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

@RestController
@RequestMapping("/api/v1")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final EnrollmentService enrollmentService;
    private final SubmissionService submissionService;

    public AssignmentController(AssignmentService assignmentService, EnrollmentService enrollmentService, SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.enrollmentService = enrollmentService;
        this.submissionService = submissionService;
    }

    @PostMapping("/lecturer/assignments")
    public ApiResponse<AssignmentResponse> create(@Valid @RequestBody CreateAssignmentRequest request) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.ok("Created successfully", assignmentService.create(username, request));
    }

    @GetMapping("/lecturer/assignments/course/{courseId}")
    public ApiResponse<Page<AssignmentResponse>> lecturerAssignmentsByCourse(@PathVariable Long courseId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Success", assignmentService.findByCourse(courseId, page, size));
    }

    @GetMapping("/student/assignments/course/{courseId}")
    public ApiResponse<Page<AssignmentResponse>> studentAssignmentsByCourse(@PathVariable Long courseId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (!enrollmentService.isStudentEnrolled(username, courseId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "You have not registered for this course so you cannot view assignments");
        }
        return ApiResponse.ok("Success", assignmentService.findByCourse(courseId, page, size));
    }

    @PostMapping(value = "/student/assignments/{assignmentId}/upload", consumes = "multipart/form-data")
    public ApiResponse<SubmissionResponse> uploadForAssignment(@PathVariable Long assignmentId, @RequestPart MultipartFile file) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.ok("Uploaded successfully", submissionService.uploadForAssignment(username, assignmentId, file));
    }
}
