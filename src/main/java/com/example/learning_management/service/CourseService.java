package com.example.learning_management.service;


import com.example.learning_management.dto.course.CourseCreateRequest;
import com.example.learning_management.dto.course.CourseUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.exception.course.CourseHasGroupsException;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.ScheduleRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import com.example.learning_management.repository.TeacherRepository;
import com.example.learning_management.specification.CourseSpecification;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.learning_management.specification.CourseSpecification.teacherIdEquals;


@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;
    private final StudyGroupRepository studyGroupRepository;
    private final ScheduleRepository scheduleRepository;

    public CourseService(CourseRepository courseRepository,
                         TeacherRepository teacherRepository,
                         CourseMapper mapper,
                         StudyGroupRepository studyGroupRepository,
                         ScheduleRepository scheduleRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.courseMapper = mapper;
        this.studyGroupRepository = studyGroupRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Course create(@NonNull CourseCreateRequest request) {
        Teacher teacher =
                teacherRepository.findByIdAndDeletedAtIsNull(request.teacherId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Преподаватель с id " + request.teacherId() + " не найден."
                        ));
        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacher);
        Course savedCourse = courseRepository.save(course);
        return savedCourse;
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return courseRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException(
                "Курс с id " + id + " не найден."
        ));
    }

    @Transactional
    public Course update(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курс с id " + id + " не найден."
                ));
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподаватель с id " + request.teacherId() + " не найден."
                ));
        boolean teacherChanged =
                !course.getTeacher().getId().equals(teacher.getId());
        boolean hasActiveSchedules =
                scheduleRepository.existsByCourse_IdAndDeletedAtIsNull(id);
        if (teacherChanged && hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя изменить преподавателя курса: есть активные занятия."
            );
        }
        courseMapper.updateEntity(request, course);
        course.setTeacher(teacher);
        return course;
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курс с id " + id + " не найден."
                ));
        boolean hasGroups = studyGroupRepository.existsByCourses_IdAndDeletedAtIsNull(id);
        if (hasGroups) {
            throw new CourseHasGroupsException(id);
        }
        boolean hasActiveSchedules =
                scheduleRepository.existsByCourse_IdAndDeletedAtIsNull(id);
        if (hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя удалить курс: есть активные занятия."
            );
        }
        course.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<Course> findAll(String name, Long teacherId, Pageable pageable) {
        Specification<Course> specification = CourseSpecification.isActive();
        if (name != null && !name.isBlank()) {
            specification = specification.and(CourseSpecification.nameContains(name));
        }
        if (teacherId != null) {
            specification = specification.and(teacherIdEquals(teacherId));
        }
        return courseRepository.findAll(specification, pageable);
    }
}