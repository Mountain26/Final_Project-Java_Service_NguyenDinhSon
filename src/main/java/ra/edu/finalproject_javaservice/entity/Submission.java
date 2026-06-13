package ra.edu.finalproject_javaservice.entity;

import ra.edu.finalproject_javaservice.common.AuditAwareEntity;
import jakarta.persistence.*;

@Entity
public class Submission extends AuditAwareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = true)
    private Assignment assignment;
    @ManyToOne(optional = false) private Course course;
    @ManyToOne(optional = false) private User student;
    @Column(nullable = true) private String reportUrl;
    private Double score;
    @Column(length = 2000) private String feedback;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private SubmissionStatus status = SubmissionStatus.PENDING;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
}
