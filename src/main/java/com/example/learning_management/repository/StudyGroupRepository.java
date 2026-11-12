package com.example.learning_management.repository;

import com.example.learning_management.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> {


    List<StudyGroup> findAllByIdInAndDeletedFalse(Set<Long> groupIds);

    Optional<StudyGroup> findByIdAndDeletedFalse(Long id);

    boolean existsByCourses_IdAndDeletedFalse(Long courseId);

    boolean existsByIdAndCourses_IdAndDeletedFalse(Long groupId, Long courseId);
}
