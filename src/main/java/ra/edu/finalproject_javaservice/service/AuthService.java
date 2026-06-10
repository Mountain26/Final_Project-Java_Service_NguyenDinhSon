package ra.edu.finalproject_javaservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.finalproject_javaservice.dto.*;
import ra.edu.finalproject_javaservice.entity.TokenBlacklist;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.exception.BadRequestException;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.TokenBlacklistRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;
import ra.edu.finalproject_javaservice.security.JwtService;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlacklistRepository tokenBlacklistRepository) {
        this.authenticationManager = authenticationManager; this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; this.tokenBlacklistRepository = tokenBlacklistRepository;
    }
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username()).orElseThrow();
        if (!user.isActive()) throw new BadRequestException("Account is inactive");
        return new AuthResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user), user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) throw new ConflictException("Username already exists");
        if (userRepository.existsByEmail(request.email())) throw new ConflictException("Email already exists");
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(Role.STUDENT);
        user.setActive(true);
        user = userRepository.save(user);
        return new AuthResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user), user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }

    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) throw new BadRequestException("Missing refresh token");
        if (tokenBlacklistRepository.existsByTokenString(refreshToken)) throw new BadRequestException("Token is revoked");
        if (!jwtService.isValid(refreshToken)) throw new BadRequestException("Invalid refresh token");
        if (!"refresh".equals(jwtService.extractTokenType(refreshToken))) throw new BadRequestException("Token is not a refresh token");
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.isActive()) throw new BadRequestException("Account is inactive");
        revokeToken(refreshToken);
        return new AuthResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user), user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }
    public void logout(String bearerToken) {
        logout(bearerToken, null);
    }
    public void logout(String bearerToken, String refreshToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) throw new BadRequestException("Missing bearer token");
        String token = bearerToken.substring(7);
        revokeToken(token);
        if (refreshToken != null && !refreshToken.isBlank()) {
            revokeToken(refreshToken);
        }
    }
    public AuthResponse changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) throw new BadRequestException("Old password is incorrect");
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new AuthResponse(null, null, user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private void revokeToken(String token) {
        if (tokenBlacklistRepository.existsByTokenString(token)) throw new ConflictException("Token already revoked");
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setTokenString(token);
        blacklist.setRevokedAt(LocalDateTime.now());
        tokenBlacklistRepository.save(blacklist);
    }
}
