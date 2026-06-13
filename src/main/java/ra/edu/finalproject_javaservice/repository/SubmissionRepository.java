package ra.edu.finalproject_javaservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.finalproject_javaservice.entity.Submission;
import ra.edu.finalproject_javaservice.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByStudent_Id(Long studentId);
    Page<Submission> findByStudent_Id(Long studentId, Pageable pageable);
    java.util.Optional<Submission> findByStudent_IdAndCourse_IdAndStatusNot(Long studentId, Long courseId, SubmissionStatus status);
    Page<Submission> findByStatus(SubmissionStatus status, Pageable pageable);
    Page<Submission> findByCourse_Id(Long courseId, Pageable pageable);
}
