package ra.edu.finalproject_javaservice.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ra.edu.finalproject_javaservice.dto.CreateCourseRequest;
import ra.edu.finalproject_javaservice.dto.CourseResponse;
import ra.edu.finalproject_javaservice.dto.UpdateCourseRequest;
import ra.edu.finalproject_javaservice.entity.Course;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    public CourseService(CourseRepository courseRepository) { this.courseRepository = courseRepository; }
    public CourseResponse create(CreateCourseRequest request) {
        if (courseRepository.existsByCourseCode(request.courseCode())) throw new ConflictException("Course code already exists");
        Course c = new Course();
        c.setCourseCode(request.courseCode());
        c.setCourseName(request.courseName());
        c.setCredit(request.credit());
        courseRepository.save(c);
        return new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit());
    }
    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course c = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        if (request.courseCode() != null && !request.courseCode().isBlank()) {
            if (!request.courseCode().equals(c.getCourseCode()) && courseRepository.existsByCourseCode(request.courseCode())) {
                throw new ConflictException("Course code already exists");
            }
            c.setCourseCode(request.courseCode());
        }
        if (request.courseName() != null && !request.courseName().isBlank()) {
            c.setCourseName(request.courseName());
        }
        if (request.credit() != null) {
            c.setCredit(request.credit());
        }
        c = courseRepository.save(c);
        return new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit());
    }
    public void delete(Long id) { courseRepository.deleteById(id); }
    public Page<CourseResponse> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = (keyword == null || keyword.isBlank())
                ? courseRepository.findAll(pageable)
                : courseRepository.findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(keyword, keyword, pageable);
        return coursesPage.map(c -> new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit()));
    }
}
