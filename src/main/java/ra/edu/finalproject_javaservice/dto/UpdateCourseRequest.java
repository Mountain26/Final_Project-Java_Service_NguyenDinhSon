package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCourseRequest(@NotBlank String courseCode, @NotBlank String courseName, @NotNull @Positive Integer credit) {}
