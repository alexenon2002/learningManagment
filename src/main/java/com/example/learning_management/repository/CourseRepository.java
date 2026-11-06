package com.example.learning_management.repository;

import com.example.learning_management.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> , JpaSpecificationExecutor<Course> {


     Optional<Course> findByIdAndDeletedAtIsNull(Long id);

     boolean existsByTeacher_IdAndDeletedAtIsNull(Long teacherId);
}
