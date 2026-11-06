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
    Optional<Schedule> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByCourse_IdAndDeletedAtIsNull(Long courseId);

    boolean existsByGroup_IdAndDeletedAtIsNull(Long groupId);

    boolean existsByGroup_IdAndCourse_IdAndDeletedAtIsNull(Long groupId, Long courseId);

    @Modifying
    @Query("""
        update Schedule schedule
        set schedule.deletedAt = :now,
            schedule.updatedAt = :now
        where schedule.deletedAt is null
          and schedule.endsAt < :cutoff
        """)
    int markDeletedByEndsAtBefore(
            @Param("cutoff") LocalDateTime cutoff,
            @Param("now") LocalDateTime now
    );
}
