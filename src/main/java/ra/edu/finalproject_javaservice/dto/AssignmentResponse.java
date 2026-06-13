package ra.edu.finalproject_javaservice.dto;

import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        Long courseId,
        Long lecturerId,
        String title,
        String description,
        LocalDateTime dueDate,
        Double maxScore
) {}
