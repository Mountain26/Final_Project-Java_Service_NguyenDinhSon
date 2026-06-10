package ra.edu.finalproject_javaservice.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ra.edu.finalproject_javaservice.dto.CreateCourseRequest;
import ra.edu.finalproject_javaservice.dto.CourseResponse;
import ra.edu.finalproject_javaservice.dto.UpdateCourseRequest;
import ra.edu.finalproject_javaservice.entity.Course;
import ra.edu.finalproject_javaservice.exception.BadRequestException;
import ra.edu.finalproject_javaservice.exception.ConflictException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.CourseRepository;

import java.util.List;
import java.util.stream.Stream;

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
        c.setCourseCode(request.courseCode());
        c.setCourseName(request.courseName());
        c.setCredit(request.credit());
        c = courseRepository.save(c);
        return new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit());
    }
    public void delete(Long id) { courseRepository.deleteById(id); }
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(c -> new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit())).toList();
    }
    public Page<CourseResponse> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseResponse> filtered = courseRepository.findAll().stream()
                .filter(c -> keyword == null || keyword.isBlank()
                        || c.getCourseCode().contains(keyword)
                        || c.getCourseName().contains(keyword))
                .map(c -> new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit()))
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<CourseResponse> content = start >= filtered.size() ? List.of() : filtered.subList(start, end);
        return new PageImpl<>(content, pageable, filtered.size());
    }
    public List<CourseResponse> findAll(String keyword) {
        Stream<Course> stream = courseRepository.findAll().stream();
        if (keyword != null && !keyword.isBlank()) {
            stream = stream.filter(c -> c.getCourseCode().contains(keyword) || c.getCourseName().contains(keyword));
        }
        return stream.map(c -> new CourseResponse(c.getId(), c.getCourseCode(), c.getCourseName(), c.getCredit())).toList();
    }
}
