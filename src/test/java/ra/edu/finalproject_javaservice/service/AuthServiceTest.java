package ra.edu.finalproject_javaservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.edu.finalproject_javaservice.dto.AuthRequest;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.repository.TokenBlacklistRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;
import ra.edu.finalproject_javaservice.security.JwtService;

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
        when(tokenBlacklistRepository.existsByTokenString("old")).thenReturn(false);
        when(jwtService.isValid("old")).thenReturn(true);
        when(jwtService.extractTokenType("old")).thenReturn("refresh");
        when(jwtService.extractUsername("old")).thenReturn("u1");
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("newA");
        when(jwtService.generateRefreshToken(user)).thenReturn("newR");

        var result = authService.refresh("old");

        assertEquals("newA", result.accessToken());
        verify(tokenBlacklistRepository).save(any());
    }
}
