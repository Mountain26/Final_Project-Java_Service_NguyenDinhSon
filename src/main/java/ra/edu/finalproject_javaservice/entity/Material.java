package ra.edu.finalproject_javaservice.entity;

import ra.edu.finalproject_javaservice.common.AuditAwareEntity;
import jakarta.persistence.*;

@Entity
public class Material extends AuditAwareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Course course;
    @Column(nullable = false) private String materialName;
    @Column(nullable = false) private String fileUrl;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}
