package ra.edu.finalproject_javaservice.dto;

public record SubmissionResponse(Long id, Long courseId, Long studentId, String reportUrl, Double score, String feedback, String status) {}
