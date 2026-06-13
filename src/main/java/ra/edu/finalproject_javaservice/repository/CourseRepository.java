package ra.edu.finalproject_javaservice.repository;

import ra.edu.finalproject_javaservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByCourseCode(String courseCode);
    boolean existsByCourseCode(String courseCode);
    Page<Course> findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(String courseCode, String courseName, Pageable pageable);
}
