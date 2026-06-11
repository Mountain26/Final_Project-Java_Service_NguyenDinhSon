package ra.edu.finalproject_javaservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AdminService adminService;

    @Test
    void usersPaginationWorks() {
        User user = new User();
        user.setId(1L); user.setUsername("u1"); user.setEmail("u1@mail.com"); user.setRole(Role.ADMIN); user.setActive(true);
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        var page = adminService.users(null, 0, 10);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void seedAdminCreatesOrFindsAdmin() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("hashed");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        var admin = adminService.seedAdmin();
        assertEquals("admin", admin.getUsername());
    }
}
