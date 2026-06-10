package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMaterialRequest(@NotNull Long courseId, @NotBlank String materialName, @NotBlank String fileUrl) {}
