package com.example.learning_management.repository;

import com.example.learning_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    public boolean existsByGroups_Id(Long groupId);
}
