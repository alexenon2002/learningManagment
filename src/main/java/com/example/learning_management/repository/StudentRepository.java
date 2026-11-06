package com.example.learning_management.repository;

import com.example.learning_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> , JpaSpecificationExecutor<Student> {

     boolean existsByGroups_IdAndDeletedAtIsNull(Long groupId);

     Optional<Student> findByIdAndDeletedAtIsNull(Long id);

}
