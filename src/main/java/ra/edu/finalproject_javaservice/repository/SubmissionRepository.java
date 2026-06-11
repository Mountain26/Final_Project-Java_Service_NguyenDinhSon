package ra.edu.finalproject_javaservice.repository;

import ra.edu.finalproject_javaservice.entity.Submission;
import ra.edu.finalproject_javaservice.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent_Id(Long studentId);
    java.util.Optional<Submission> findByStudent_IdAndCourse_IdAndStatusNot(Long studentId, Long courseId, SubmissionStatus status);
    List<Submission> findByStatus(SubmissionStatus status);
    List<Submission> findByCourse_Id(Long courseId);
}
