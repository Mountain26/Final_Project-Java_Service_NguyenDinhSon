package ra.edu.finalproject_javaservice.repository;

import ra.edu.finalproject_javaservice.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    java.util.List<Material> findByCourse_Id(Long courseId);
}
