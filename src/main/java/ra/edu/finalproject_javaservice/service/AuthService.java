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
    private final OtpService otpService;
    private final MailService mailService;
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlacklistRepository tokenBlacklistRepository, OtpService otpService, MailService mailService) {
        this.authenticationManager = authenticationManager; this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; this.tokenBlacklistRepository = tokenBlacklistRepository; this.otpService = otpService; this.mailService = mailService;
    }
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username()).orElseThrow();
        if (!user.isActive()) throw new BadRequestException("Account is inactive");
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateAccessToken(user), refreshToken, user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
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
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateAccessToken(user), refreshToken, user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }

    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) throw new BadRequestException("Missing refresh token");
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        if (!user.isActive()) throw new BadRequestException("Account is inactive");
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateAccessToken(user), newRefreshToken, user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }
    public void logout(String bearerToken) {
        logout(bearerToken, null);
    }
    public void logout(String bearerToken, String refreshToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) throw new BadRequestException("Missing bearer token");
        String token = bearerToken.substring(7);
        revokeToken(token);
        if (refreshToken != null && !refreshToken.isBlank()) {
            userRepository.findByRefreshToken(refreshToken).ifPresent(u -> {
                u.setRefreshToken(null);
                userRepository.save(u);
            });
        }
    }
    public AuthResponse changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) throw new BadRequestException("Old password is incorrect");
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new AuthResponse(null, null, user.getRole().name(), user.getId(), user.getUsername(), user.getEmail());
    }
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new NotFoundException("Khong tim thay gmail da dang ky tai khoan"));
        String otp = otpService.generate(user.getEmail());
        mailService.sendOtp(user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new NotFoundException("Khong tim thay gmail da dang ky tai khoan"));
        otpService.verify(request.email(), request.otp());
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
