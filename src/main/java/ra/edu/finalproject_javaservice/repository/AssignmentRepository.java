package ra.edu.finalproject_javaservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.finalproject_javaservice.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Page<Assignment> findByCourse_Id(Long courseId, Pageable pageable);
}
