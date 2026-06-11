package ra.edu.finalproject_javaservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ra.edu.finalproject_javaservice.dto.CreateUserRequest;
import ra.edu.finalproject_javaservice.dto.UpdateUserRequest;
import ra.edu.finalproject_javaservice.dto.UserResponse;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

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
        Specification<User> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String like = "%" + keyword.toLowerCase() + "%";
            var predicates = java.util.List.of(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("email")), like)
            );
            try {
                Role role = Role.valueOf(keyword.toUpperCase());
                return cb.or(
                        predicates.get(0),
                        predicates.get(1),
                        cb.equal(root.get("role"), role)
                );
            } catch (IllegalArgumentException ex) {
                return cb.or(predicates.get(0), predicates.get(1));
            }
        };
        Page<User> usersPage = userRepository.findAll(spec, pageable);
        return usersPage.map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive()));
    }
    public List<UserResponse> users(String keyword) {
        Stream<User> stream = userRepository.findAll().stream();
        if (keyword != null && !keyword.isBlank()) {
            stream = stream.filter(u -> u.getUsername().contains(keyword) || u.getEmail().contains(keyword) || u.getRole().name().contains(keyword.toUpperCase()));
        }
        return stream.map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive())).toList();
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
