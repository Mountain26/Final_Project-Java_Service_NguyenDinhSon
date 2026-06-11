package ra.edu.finalproject_javaservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ra.edu.finalproject_javaservice.dto.CreateUserRequest;
import ra.edu.finalproject_javaservice.dto.UpdateUserRequest;
import ra.edu.finalproject_javaservice.dto.UserResponse;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.UserRepository;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) { this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; }
    public List<UserResponse> users() {
        return userRepository.findAll().stream().map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive())).toList();
    }
    public Page<UserResponse> users(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;
        if (keyword == null || keyword.isBlank()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            try {
                Role role = Role.valueOf(keyword.toUpperCase());
                usersPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRole(keyword, keyword, role, pageable);
            } catch (IllegalArgumentException ex) {
                usersPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
            }
        }
        return usersPage.map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive()));
    }
    public List<UserResponse> users(String keyword) {
        return (keyword == null || keyword.isBlank()
                ? userRepository.findAll()
                : resolveUsers(keyword))
                .stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive()))
                .toList();
    }

    private java.util.List<User> resolveUsers(String keyword) {
        try {
            Role role = Role.valueOf(keyword.toUpperCase());
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRole(keyword, keyword, role);
        } catch (IllegalArgumentException ex) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        }
    }
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) throw new ConflictException("Username already exists");
        if (userRepository.existsByEmail(request.email())) throw new ConflictException("Email already exists");
        User u = new User();
        u.setUsername(request.username());
        u.setPasswordHash(passwordEncoder.encode(request.password()));
        u.setEmail(request.email());
        u.setRole(Role.valueOf(request.role()));
        u.setActive(true);
        u = userRepository.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive());
    }
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User u = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        u.setEmail(request.email());
        u.setActive(request.active());
        u = userRepository.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive());
    }
    public void deleteUser(Long id) { userRepository.deleteById(id); }
    public User seedAdmin() {
        return userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setEmail("admin@gmail.com");
            u.setPasswordHash(passwordEncoder.encode("admin123"));
            u.setRole(Role.ADMIN);
            u.setActive(true);
            return userRepository.save(u);
        });
    }
}
