package com.example.learning_management.service;

import com.example.learning_management.dto.schedule.ScheduleCreateRequest;
import com.example.learning_management.dto.schedule.ScheduleResponse;
import com.example.learning_management.dto.schedule.ScheduleUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Schedule;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.schedule.InvalidScheduleTimeException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.lookup.CourseLookup;
import com.example.learning_management.mapper.ScheduleMapper;
import com.example.learning_management.repository.ScheduleRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import com.example.learning_management.specification.ScheduleSpecification;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final ScheduleMapper scheduleMapper;
    private final CourseLookup courseLookup;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           StudyGroupRepository studyGroupRepository,
                           ScheduleMapper scheduleMapper,
                           CourseLookup courseLookup) {
        this.scheduleRepository = scheduleRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.scheduleMapper = scheduleMapper;
        this.courseLookup = courseLookup;
    }

    @Transactional
    public ScheduleResponse create(@NonNull ScheduleCreateRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new InvalidScheduleTimeException("Некорректное время занятия.");
        }
        StudyGroup group = studyGroupRepository.findByIdAndDeletedFalse(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с id " + request.groupId() + " не найдена."
                ));
        Course course = courseLookup.getActiveCourseOrThrow(request.courseId());
        boolean isEnrolled = studyGroupRepository
                .existsByIdAndCourses_IdAndDeletedFalse(
                        request.groupId(), request.courseId());
        if (!isEnrolled) {
            throw new ScheduleConflictException("Группа не записана на курс.");
        }
        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(course.getTeacher());
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(savedSchedule);
    }

    private @NonNull Schedule getActiveScheduleOrThrow(Long id) {
        return scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Занятие с id " + id + " не найдено."
                ));
    }

    @Transactional(readOnly = true)
    public ScheduleResponse findById(Long id) {
        return scheduleMapper.toResponse(getActiveScheduleOrThrow(id));

    }

    @Transactional
    public ScheduleResponse update(Long id, @NonNull ScheduleUpdateRequest request) {
        Schedule schedule = getActiveScheduleOrThrow(id);
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new InvalidScheduleTimeException("Некорректное время занятия.");
        }
        StudyGroup group = studyGroupRepository.findByIdAndDeletedFalse(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с id " + request.groupId() + " не найдена."
                ));
        Course course = courseLookup.getActiveCourseOrThrow(request.courseId());
        boolean isEnrolled = studyGroupRepository
                .existsByIdAndCourses_IdAndDeletedFalse(
                        request.groupId(), request.courseId());
        if (!isEnrolled) {
            throw new ScheduleConflictException("Группа не записана на курс.");
        }
        scheduleMapper.updateEntity(request, schedule);
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(course.getTeacher());
        return scheduleMapper.toResponse(schedule);
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = getActiveScheduleOrThrow(id);
        schedule.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponse> findAll(Long groupId, Long courseId, Long teacherId, Pageable pageable) {
        Specification<Schedule> specification = ScheduleSpecification.isActive();
        if (groupId != null) {
            specification = specification.and(ScheduleSpecification.groupIdEquals(groupId));
        }
        if (courseId != null) {
            specification = specification.and(ScheduleSpecification.courseIdEquals(courseId));
        }
        if (teacherId != null) {
            specification = specification.and(ScheduleSpecification.teacherIdEquals(teacherId));
        }
        return scheduleRepository.findAll(specification, pageable).map(scheduleMapper::toResponse);
    }
}
