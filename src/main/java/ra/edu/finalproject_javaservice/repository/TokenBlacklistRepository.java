package ra.edu.finalproject_javaservice.repository;

import ra.edu.finalproject_javaservice.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenString(String tokenString);
    Optional<TokenBlacklist> findByTokenString(String tokenString);
}
