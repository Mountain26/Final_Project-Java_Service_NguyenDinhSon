package ra.edu.finalproject_javaservice.entity;

import ra.edu.finalproject_javaservice.common.AuditAwareEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TokenBlacklist extends AuditAwareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 2000)
    private String tokenString;
    @Column(nullable = false)
    private LocalDateTime revokedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenString() { return tokenString; }
    public void setTokenString(String tokenString) { this.tokenString = tokenString; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}
