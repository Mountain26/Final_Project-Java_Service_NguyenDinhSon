package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank String username, @NotBlank String newPassword) {}
