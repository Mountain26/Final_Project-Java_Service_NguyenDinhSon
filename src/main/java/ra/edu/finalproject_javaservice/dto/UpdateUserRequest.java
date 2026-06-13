package ra.edu.finalproject_javaservice.dto;

public record UpdateUserRequest(String username, String password, String email, String role, Boolean active) {}
