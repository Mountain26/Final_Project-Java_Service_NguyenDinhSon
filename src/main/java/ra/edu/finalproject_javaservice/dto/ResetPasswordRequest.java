package ra.edu.finalproject_javaservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@Email @NotBlank String email,
                                   @NotBlank String otp,
                                   @NotBlank String newPassword) {}
