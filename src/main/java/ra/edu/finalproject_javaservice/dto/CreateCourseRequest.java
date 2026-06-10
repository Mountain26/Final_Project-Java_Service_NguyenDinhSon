package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.*;

public record CreateCourseRequest(
        @NotBlank String courseCode,
        @NotBlank String courseName,
        @NotNull @Positive Integer credit
) {}
