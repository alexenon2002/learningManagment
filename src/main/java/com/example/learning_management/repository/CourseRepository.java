package com.example.learning_management.repository;

import com.example.learning_management.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    public boolean existsByTeacher_Id(Long teacherId);

    public Page<Course> findByGroups_Id(Long groupId, Pageable pageable);
}
