package ra.edu.finalproject_javaservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.dto.AuthResponse;
import ra.edu.finalproject_javaservice.dto.AuthRequest;
import ra.edu.finalproject_javaservice.dto.ForgotPasswordRequest;
import ra.edu.finalproject_javaservice.dto.RefreshRequest;
import ra.edu.finalproject_javaservice.dto.LogoutRequest;
import ra.edu.finalproject_javaservice.dto.ResetPasswordRequest;
import ra.edu.finalproject_javaservice.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock AuthService authService;
    @InjectMocks AuthController authController;

    @Test
    void loginReturnsSuccess() {
        when(authService.login(new AuthRequest("u", "p")))
                .thenReturn(new AuthResponse("a", "r", "STUDENT", 1L, "u", "e"));
        var response = authController.login(new AuthRequest("u", "p"));
        assertTrue(response.success());
        assertEquals("a", response.data().accessToken());
        verify(authService).login(new AuthRequest("u", "p"));
    }

    @Test
    void refreshReturnsSuccess() {
        when(authService.refresh("r")).thenReturn(new AuthResponse("a", "r2", "STUDENT", 1L, "u", "e"));
        var response = authController.refresh(new RefreshRequest("r"));
        assertTrue(response.success());
        assertEquals("r2", response.data().refreshToken());
    }

    @Test
    void logoutPassesRefreshToken() {
        var response = authController.logout("Bearer access", new LogoutRequest("refresh"));
        assertTrue(response.success());
        verify(authService).logout("Bearer access", "refresh");
    }

    @Test
    void forgotPasswordReturnsSuccess() {
        var response = authController.forgotPassword(new ForgotPasswordRequest("u@mail.com"));
        assertTrue(response.success());
        verify(authService).forgotPassword(new ForgotPasswordRequest("u@mail.com"));
    }

    @Test
    void resetPasswordReturnsSuccess() {
        var response = authController.resetPassword(new ResetPasswordRequest("u@mail.com", "123456", "newPass"));
        assertTrue(response.success());
        verify(authService).resetPassword(new ResetPasswordRequest("u@mail.com", "123456", "newPass"));
    }
}
