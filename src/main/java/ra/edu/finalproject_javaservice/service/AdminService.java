package ra.edu.finalproject_javaservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import ra.edu.finalproject_javaservice.dto.CreateUserRequest;
import ra.edu.finalproject_javaservice.dto.UpdateUserRequest;
import ra.edu.finalproject_javaservice.dto.UserResponse;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.ForbiddenException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.EnrollmentRepository;
import ra.edu.finalproject_javaservice.repository.SubmissionRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminService(UserRepository userRepository, EnrollmentRepository enrollmentRepository, SubmissionRepository submissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (keyword != null && !keyword.isBlank() && usersPage.isEmpty()) {
            throw new NotFoundException("Không tìm thấy người dùng phù hợp với từ khóa '" + keyword + "'");
        }
        return usersPage.map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive()));
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
        if (request.username() != null && !request.username().isBlank()) {
            if (!request.username().equals(u.getUsername()) && userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Username already exists");
            }
            u.setUsername(request.username());
        }
        if (request.password() != null && !request.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.email() != null && !request.email().isBlank()) {
            if (!request.email().equals(u.getEmail()) && userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Email already exists");
            }
            u.setEmail(request.email());
        }
        if (request.role() != null && !request.role().isBlank()) {
            u.setRole(Role.valueOf(request.role().toUpperCase()));
        }
        if (request.active() != null) {
            if (u.getRole() == Role.ADMIN && !request.active()) {
                throw new ForbiddenException("Admin account cannot be disabled");
            }
            u.setActive(request.active());
        }
        u = userRepository.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), u.isActive());
    }
    public void deleteUser(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (u.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Admin account cannot be deleted");
        }
        if (enrollmentRepository.existsByStudent_Id(id) || submissionRepository.existsByStudent_Id(id)) {
            throw new ConflictException("User cannot be deleted because this account already has enrollments or submissions");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("User cannot be deleted because this account is still referenced by other data");
        }
    }
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
