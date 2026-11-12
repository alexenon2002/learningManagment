package com.example.learning_management.lookup;

import com.example.learning_management.entity.Course;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.repository.CourseRepository;
import org.springframework.stereotype.Component;

@Component
public class CourseLookup {
    private final CourseRepository courseRepository;

    public CourseLookup(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getActiveCourseOrThrow(Long id) {
        return courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курс с id " + id + " не найден."
                ));
    }
}
