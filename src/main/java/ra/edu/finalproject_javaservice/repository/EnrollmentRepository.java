package ra.edu.finalproject_javaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.finalproject_javaservice.entity.Enrollment;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    boolean existsByStudent_Id(Long studentId);
    List<Enrollment> findByStudent_Id(Long studentId);
}
