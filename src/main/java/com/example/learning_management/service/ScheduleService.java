package com.example.learning_management.service;

import com.example.learning_management.dto.schedule.ScheduleCreateRequest;
import com.example.learning_management.dto.schedule.ScheduleUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Schedule;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.schedule.InvalidScheduleTimeException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.mapper.ScheduleMapper;
import com.example.learning_management.repository.CourseRepository;
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
    private final CourseRepository courseRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           StudyGroupRepository studyGroupRepository,
                           CourseRepository courseRepository,
                           ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.courseRepository = courseRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Transactional
    public Schedule create(@NonNull ScheduleCreateRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new InvalidScheduleTimeException("Некорректное время занятия.");
        }
        StudyGroup group = studyGroupRepository.findByIdAndDeletedAtIsNull(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с id " + request.groupId() + " не найдена."
                ));
        Course course = courseRepository.findByIdAndDeletedAtIsNull(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курс с id " + request.courseId() + " не найден."
                ));
        boolean isEnrolled = studyGroupRepository
                .existsByIdAndCourses_IdAndDeletedAtIsNull(
                        request.groupId(), request.courseId());
        if (!isEnrolled) {
            throw new ScheduleConflictException("Группа не записана на курс.");
        }
        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(course.getTeacher());
        return scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public Schedule findById(Long id) {
        return scheduleRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Занятие с id " + id + " не найдено."
                ));

    }

    @Transactional
    public Schedule update(Long id, @NonNull ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Занятие с id " + id + " не найдено."
                ));
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new InvalidScheduleTimeException("Некорректное время занятия.");
        }
        StudyGroup group = studyGroupRepository.findByIdAndDeletedAtIsNull(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с id " + request.groupId() + " не найдена."
                ));
        Course course = courseRepository.findByIdAndDeletedAtIsNull(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курс с id " + request.courseId() + " не найден."
                ));
        boolean isEnrolled = studyGroupRepository
                .existsByIdAndCourses_IdAndDeletedAtIsNull(
                        request.groupId(), request.courseId());
        if (!isEnrolled) {
            throw new ScheduleConflictException("Группа не записана на курс.");
        }
        scheduleMapper.updateEntity(request, schedule);
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(course.getTeacher());
        return schedule;
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Занятие с id " + id + " не найдено."
                ));
        schedule.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<Schedule> findAll(Long groupId, Long courseId, Long teacherId, Pageable pageable) {
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
        return scheduleRepository.findAll(specification, pageable);
    }
}
