package ra.edu.finalproject_javaservice.dto;

public record AuthResponse(String accessToken, String refreshToken, String role, Long userId, String username, String email) {}
