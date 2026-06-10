package ra.edu.finalproject_javaservice.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.GradeRequest;
import ra.edu.finalproject_javaservice.dto.MaterialResponse;
import ra.edu.finalproject_javaservice.dto.SubmissionResponse;
import ra.edu.finalproject_javaservice.service.MaterialService;
import ra.edu.finalproject_javaservice.service.SubmissionService;

@RestController
@RequestMapping("/api/v1/lecturer")
public class LecturerController {
    private final SubmissionService submissionService;
    private final MaterialService materialService;
    public LecturerController(SubmissionService submissionService, MaterialService materialService) {
        this.submissionService = submissionService;
        this.materialService = materialService;
    }

    @PostMapping("/grades")
    public ApiResponse<SubmissionResponse> grade(@Valid @RequestBody GradeRequest request) {
        return ApiResponse.ok("Graded successfully", submissionService.grade(request));
    }
    @PutMapping("/submissions/{submissionId}/return")
    public ApiResponse<SubmissionResponse> returnSubmission(@PathVariable Long submissionId, @RequestParam String feedback) {
        return ApiResponse.ok("Returned successfully", submissionService.returnSubmission(submissionId, feedback));
    }
    @GetMapping("/submissions/course/{courseId}")
    public ApiResponse<?> submissionsByCourse(@PathVariable Long courseId) {
        return ApiResponse.ok("Success", submissionService.findByCourse(courseId));
    }

    @PostMapping(value = "/materials/upload", consumes = "multipart/form-data")
    public ApiResponse<MaterialResponse> uploadMaterial(@RequestParam Long courseId, @RequestParam String materialName, @RequestPart MultipartFile file) {
        return ApiResponse.ok("Uploaded successfully", materialService.upload(courseId, materialName, file));
    }
    @GetMapping("/materials/course/{courseId}")
    public ApiResponse<?> materialsByCourse(@PathVariable Long courseId) {
        return ApiResponse.ok("Success", materialService.findByCourse(courseId));
    }
    @DeleteMapping("/materials/{id}")
    public ApiResponse<Void> deleteMaterial(@PathVariable Long id) { materialService.delete(id); return ApiResponse.ok("Deleted", null); }
}
