package com.example.learning_management.service;


import com.example.learning_management.dto.course.CourseCreateRequest;
import com.example.learning_management.dto.course.CourseUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.exception.course.CourseHasGroupsException;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.lookup.CourseLookup;
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
import com.example.learning_management.dto.course.CourseResponse;

import static com.example.learning_management.specification.CourseSpecification.teacherIdEquals;


@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;
    private final StudyGroupRepository studyGroupRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseLookup courseLookup;

    public CourseService(CourseRepository courseRepository,
                         TeacherRepository teacherRepository,
                         CourseMapper mapper,
                         StudyGroupRepository studyGroupRepository,
                         ScheduleRepository scheduleRepository,
                         CourseLookup courseLookup) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.courseMapper = mapper;
        this.studyGroupRepository = studyGroupRepository;
        this.scheduleRepository = scheduleRepository;
        this.courseLookup = courseLookup;
    }

    @Transactional
    public CourseResponse create(@NonNull CourseCreateRequest request) {
        Teacher teacher =
                teacherRepository.findByIdAndDeletedFalse(request.teacherId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Преподаватель с id " + request.teacherId() + " не найден."
                        ));
        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacher);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseLookup.getActiveCourseOrThrow(id);
        return courseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponse update(Long id, @NonNull CourseUpdateRequest request) {
        Course course = courseLookup.getActiveCourseOrThrow(id);
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподаватель с id " + request.teacherId() + " не найден."
                ));
        boolean teacherChanged =
                !course.getTeacher().getId().equals(teacher.getId());
        boolean hasActiveSchedules =
                scheduleRepository.existsByCourse_IdAndDeletedFalse(id);
        if (teacherChanged && hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя изменить преподавателя курса: есть активные занятия."
            );
        }
        courseMapper.updateEntity(request, course);
        course.setTeacher(teacher);
        return courseMapper.toResponse(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseLookup.getActiveCourseOrThrow(id);
        boolean hasGroups = studyGroupRepository.existsByCourses_IdAndDeletedFalse(id);
        if (hasGroups) {
            throw new CourseHasGroupsException(id);
        }
        boolean hasActiveSchedules =
                scheduleRepository.existsByCourse_IdAndDeletedFalse(id);
        if (hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя удалить курс: есть активные занятия."
            );
        }
        course.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> findAll(String name, Long teacherId, Pageable pageable) {
        Specification<Course> specification = CourseSpecification.isActive();
        if (name != null && !name.isBlank()) {
            specification = specification.and(CourseSpecification.nameContains(name));
        }
        if (teacherId != null) {
            specification = specification.and(teacherIdEquals(teacherId));
        }
        return courseRepository.findAll(specification, pageable)
                .map(courseMapper::toResponse);
    }
}