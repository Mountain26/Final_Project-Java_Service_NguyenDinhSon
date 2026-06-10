package ra.edu.finalproject_javaservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.*;
import ra.edu.finalproject_javaservice.service.AuthService;

@RestController
@RequestMapping({"/api/auth", "/api/v1/auth"})
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Register successfully", authService.register(request));
    }
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.ok("Login successfully", authService.login(request));
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok("Token refreshed successfully", authService.refresh(request.refreshToken()));
    }
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                    @RequestBody(required = false) LogoutRequest request) {
        authService.logout(authHeader, request == null ? null : request.refreshToken());
        return ApiResponse.ok("Logout successfully", null);
    }
    @PostMapping("/change-password")
    public ApiResponse<AuthResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ApiResponse.ok("Password changed", authService.changePassword(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName(), request));
    }
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successfully", null);
    }
}
