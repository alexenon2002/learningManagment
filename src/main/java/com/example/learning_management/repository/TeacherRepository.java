package com.example.learning_management.repository;

import com.example.learning_management.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

     Optional<Teacher> findByIdAndDeletedFalse(Long id);

}
