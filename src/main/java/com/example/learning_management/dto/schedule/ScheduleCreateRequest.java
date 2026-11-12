package com.example.learning_management.dto.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ScheduleCreateRequest(@NotNull @Positive Long groupId,
                                    @NotNull @Positive Long courseId,
                                    @NotNull LocalDateTime startsAt,
                                    @NotNull LocalDateTime endsAt) {
}
