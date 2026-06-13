package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateAssignmentRequest(
        @NotNull Long courseId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @FutureOrPresent LocalDateTime dueDate,
        @NotNull @DecimalMin("0.0") Double maxScore
) {}
