package ra.edu.finalproject_javaservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.edu.finalproject_javaservice.entity.*;
import ra.edu.finalproject_javaservice.repository.CourseRepository;
import ra.edu.finalproject_javaservice.repository.UserRepository;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(UserRepository userRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").orElseGet(() -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                return userRepository.save(admin);
            });
            userRepository.findByUsername("lecturer").orElseGet(() -> {
                User lecturer = new User();
                lecturer.setUsername("lecturer");
                lecturer.setEmail("lecturer@gmail.com");
                lecturer.setPasswordHash(passwordEncoder.encode("lecturer123"));
                lecturer.setRole(Role.LECTURER);
                lecturer.setActive(true);
                return userRepository.save(lecturer);
            });
            userRepository.findByUsername("student").orElseGet(() -> {
                User student = new User();
                student.setUsername("student");
                student.setEmail("student@gmail.com");
                student.setPasswordHash(passwordEncoder.encode("student123"));
                student.setRole(Role.STUDENT);
                student.setActive(true);
                return userRepository.save(student);
            });
            courseRepository.findByCourseCode("SE100").orElseGet(() -> {
                Course course = new Course();
                course.setCourseCode("SE100");
                course.setCourseName("Software Engineering Fundamentals");
                course.setCredit(3);
                return courseRepository.save(course);
            });
        };
    }
}
