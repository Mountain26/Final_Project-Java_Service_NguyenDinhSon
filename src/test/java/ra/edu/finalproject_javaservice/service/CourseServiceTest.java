package ra.edu.finalproject_javaservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.finalproject_javaservice.entity.Course;
import ra.edu.finalproject_javaservice.repository.CourseRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock CourseRepository courseRepository;
    @InjectMocks CourseService courseService;

    @Test
    void findAllPaginationWorks() {
        Course course = new Course();
        course.setId(1L); course.setCourseCode("JAVA101"); course.setCourseName("Java"); course.setCredit(3);
        when(courseRepository.findAll()).thenReturn(List.of(course));
        var page = courseService.findAll(null, 0, 10);
        assertEquals(1, page.getTotalElements());
    }
}
