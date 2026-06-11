package ra.edu.finalproject_javaservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.edu.finalproject_javaservice.dto.AuthRequest;
import ra.edu.finalproject_javaservice.dto.ForgotPasswordRequest;
import ra.edu.finalproject_javaservice.dto.ResetPasswordRequest;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.TokenBlacklistRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;
import ra.edu.finalproject_javaservice.security.JwtService;
import ra.edu.finalproject_javaservice.service.MailService;
import ra.edu.finalproject_javaservice.service.OtpService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock AuthenticationManager authenticationManager;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock TokenBlacklistRepository tokenBlacklistRepository;
    @Mock OtpService otpService;
    @Mock MailService mailService;
    @InjectMocks AuthService authService;

    @Test
    void loginReturnsTokens() {
        User user = new User();
        user.setId(1L); user.setUsername("u1"); user.setEmail("u1@mail.com"); user.setRole(Role.STUDENT); user.setActive(true);
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        var result = authService.login(new AuthRequest("u1", "pw"));

        assertEquals("access", result.accessToken());
        assertEquals("refresh", result.refreshToken());
    }

    @Test
    void refreshRevokesOldToken() {
        User user = new User();
        user.setId(1L); user.setUsername("u1"); user.setEmail("u1@mail.com"); user.setRole(Role.STUDENT); user.setActive(true);
        user.setRefreshToken("old");
        when(userRepository.findAll()).thenReturn(java.util.List.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("newA");
        when(jwtService.generateRefreshToken(user)).thenReturn("newR");

        var result = authService.refresh("old");

        assertEquals("newA", result.accessToken());
        assertEquals("newR", result.refreshToken());
        verify(userRepository).save(user);
    }

    @Test
    void forgotPasswordSendsOtpToRegisteredEmail() {
        User user = new User();
        user.setEmail("u1@mail.com");
        when(userRepository.findByEmail("u1@mail.com")).thenReturn(Optional.of(user));
        when(otpService.generate("u1@mail.com")).thenReturn("123456");

        authService.forgotPassword(new ForgotPasswordRequest("u1@mail.com"));

        verify(mailService).sendOtp("u1@mail.com", "123456");
    }

    @Test
    void forgotPasswordRejectsUnknownEmail() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> authService.forgotPassword(new ForgotPasswordRequest("missing@mail.com")));
    }

    @Test
    void resetPasswordUsesOtpAndUpdatesPassword() {
        User user = new User();
        user.setEmail("u1@mail.com");
        user.setPasswordHash("oldHash");
        when(userRepository.findByEmail("u1@mail.com")).thenReturn(Optional.of(user));

        authService.resetPassword(new ResetPasswordRequest("u1@mail.com", "123456", "newPass"));

        verify(otpService).verify("u1@mail.com", "123456");
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
    }
}
