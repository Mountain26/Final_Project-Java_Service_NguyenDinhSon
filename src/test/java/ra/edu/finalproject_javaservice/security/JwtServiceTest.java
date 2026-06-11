package ra.edu.finalproject_javaservice.security;

import org.junit.jupiter.api.Test;
import ra.edu.finalproject_javaservice.entity.Role;
import ra.edu.finalproject_javaservice.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    private final JwtService jwtService = new JwtService(
            "change-me-change-me-change-me-change-me-change-me-change-me-256-bit",
            30,
            7
    );

    @Test
    void isValidReturnsFalseForInvalidToken() {
        assertFalse(jwtService.isValid("bad-token"));
    }

    @Test
    void isValidReturnsTrueForGeneratedToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setRole(Role.STUDENT);
        String token = jwtService.generateAccessToken(user);
        assertTrue(jwtService.isValid(token));
    }
}
