package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.*;

public record GradeRequest(@NotNull Long submissionId, @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double score, String feedback) {}
