package ra.edu.finalproject_javaservice.dto;

public record UserResponse(Long id, String username, String email, String role, boolean active) {}
