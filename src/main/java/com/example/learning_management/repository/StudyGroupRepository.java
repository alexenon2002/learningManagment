package com.example.learning_management.repository;

import com.example.learning_management.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    public boolean existsByCourses_Id(Long courseId);
}
