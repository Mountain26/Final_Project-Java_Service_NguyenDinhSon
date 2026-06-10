package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollCourseRequest(@NotNull Long courseId) {}
