package ra.edu.finalproject_javaservice.repository;

import ra.edu.finalproject_javaservice.entity.User;
import ra.edu.finalproject_javaservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRole(String username, String email, Role role, Pageable pageable);
    java.util.List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    java.util.List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRole(String username, String email, Role role);
}
