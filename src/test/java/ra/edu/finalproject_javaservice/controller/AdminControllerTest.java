package ra.edu.finalproject_javaservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ra.edu.finalproject_javaservice.dto.CourseResponse;
import ra.edu.finalproject_javaservice.dto.UserResponse;
import ra.edu.finalproject_javaservice.service.AdminService;
import ra.edu.finalproject_javaservice.service.CourseService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock AdminService adminService;
    @Mock CourseService courseService;
    @InjectMocks AdminController adminController;

    @Test
    void usersEndpointReturnsSuccess() {
        var user = new UserResponse(1L, "admin1", "admin1@mail.com", "ADMIN", true);
        when(adminService.users("adm", 0, 10)).thenReturn(new PageImpl<>(List.of(user)));
        var response = adminController.users("adm", 0, 10);
        assertTrue(response.success());
        assertEquals(1, ((org.springframework.data.domain.Page<?>) response.data()).getTotalElements());
        verify(adminService).users("adm", 0, 10);
    }

    @Test
    void coursesEndpointReturnsSuccess() {
        var course = new CourseResponse(1L, "JAVA101", "Java Core", 3);
        when(courseService.findAll("JAVA", 0, 10)).thenReturn(new PageImpl<>(List.of(course)));
        var response = adminController.courses("JAVA", 0, 10);
        assertTrue(response.success());
        assertEquals(1, ((org.springframework.data.domain.Page<?>) response.data()).getTotalElements());
        verify(courseService).findAll("JAVA", 0, 10);
    }
}
