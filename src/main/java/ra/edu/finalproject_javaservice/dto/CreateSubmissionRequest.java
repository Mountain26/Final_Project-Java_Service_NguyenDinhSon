package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSubmissionRequest(@NotNull Long courseId, @NotNull Long studentId) {}
