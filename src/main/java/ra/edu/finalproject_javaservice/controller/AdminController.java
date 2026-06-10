package ra.edu.finalproject_javaservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ra.edu.finalproject_javaservice.common.ApiResponse;
import ra.edu.finalproject_javaservice.dto.CreateCourseRequest;
import ra.edu.finalproject_javaservice.dto.CreateUserRequest;
import ra.edu.finalproject_javaservice.dto.UpdateCourseRequest;
import ra.edu.finalproject_javaservice.dto.UpdateUserRequest;
import ra.edu.finalproject_javaservice.service.AdminService;
import ra.edu.finalproject_javaservice.service.CourseService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;
    private final CourseService courseService;
    public AdminController(AdminService adminService, CourseService courseService) { this.adminService = adminService; this.courseService = courseService; }
    @GetMapping("/users")
    public ApiResponse<?> users(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Page<?> result = adminService.users(keyword, page, size);
        return ApiResponse.ok("Success", result);
    }
    @PostMapping("/users")
    public ApiResponse<?> createUser(@Valid @RequestBody CreateUserRequest request) { return ApiResponse.ok("Created", adminService.createUser(request)); }
    @PutMapping("/users/{id}")
    public ApiResponse<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) { return ApiResponse.ok("Updated", adminService.updateUser(id, request)); }
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) { adminService.deleteUser(id); return ApiResponse.ok("Deleted", null); }
    @PostMapping("/courses")
    public ApiResponse<?> createCourse(@Valid @RequestBody CreateCourseRequest request) { return ApiResponse.ok("Created", courseService.create(request)); }
    @GetMapping("/courses")
    public ApiResponse<?> courses(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Page<?> result = courseService.findAll(keyword, page, size);
        return ApiResponse.ok("Success", result);
    }
    @PutMapping("/courses/{id}")
    public ApiResponse<?> updateCourse(@PathVariable Long id, @Valid @RequestBody UpdateCourseRequest request) { return ApiResponse.ok("Updated", courseService.update(id, request)); }
    @DeleteMapping("/courses/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) { courseService.delete(id); return ApiResponse.ok("Deleted", null); }
}
