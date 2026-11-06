package com.example.learning_management.dto.schedule;

import java.time.LocalDateTime;

public record ScheduleResponse(Long id, Long groupId, Long courseId, Long teacherId,
                               LocalDateTime startsAt, LocalDateTime endsAt) {
}
