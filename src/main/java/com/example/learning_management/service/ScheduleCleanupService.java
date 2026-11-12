package com.example.learning_management.service;


import com.example.learning_management.repository.ScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ScheduleCleanupService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleCleanupService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Scheduled(cron = "${app.schedule-cleanup.cron}", zone = "UTC")
    @Transactional
    public void deleteOutdatedSchedules() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime cutoff = now.minusYears(1);

        scheduleRepository.markDeletedByEndsAtBefore(cutoff, now);
    }
}
