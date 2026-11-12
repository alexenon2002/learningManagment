package com.example.learning_management.repository;

import com.example.learning_management.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    Optional<Schedule> findByIdAndDeletedFalse(Long id);

    boolean existsByCourse_IdAndDeletedFalse(Long courseId);

    boolean existsByGroup_IdAndDeletedFalse(Long groupId);

    boolean existsByGroup_IdAndCourse_IdAndDeletedFalse(Long groupId, Long courseId);

    @Modifying
    @Query("""
            update Schedule schedule
            set schedule.deletedAt = :now,
                schedule.updatedAt = :now,
                schedule.deleted = true
            where schedule.deleted = false
              and schedule.endsAt < :cutoff
            """)
    void markDeletedByEndsAtBefore(
            @Param("cutoff") LocalDateTime cutoff,
            @Param("now") LocalDateTime now
    );
}
