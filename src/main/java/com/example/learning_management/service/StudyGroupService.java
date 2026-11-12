package com.example.learning_management.service;

import com.example.learning_management.dto.course.CourseResponse;
import com.example.learning_management.dto.study_group.StudyGroupCreateRequest;
import com.example.learning_management.dto.study_group.StudyGroupResponse;
import com.example.learning_management.dto.study_group.StudyGroupUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.exception.study_group.StudyGroupHasStudentsException;
import com.example.learning_management.lookup.CourseLookup;
import com.example.learning_management.mapper.StudyGroupMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.ScheduleRepository;
import com.example.learning_management.repository.StudentRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import com.example.learning_management.specification.CourseSpecification;
import com.example.learning_management.specification.StudyGroupSpecification;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.learning_management.mapper.CourseMapper;


@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMapper studyGroupMapper;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseMapper courseMapper;
    private final CourseLookup courseLookup;

    public StudyGroupService(StudyGroupRepository studyGroupRepository,
                             StudyGroupMapper studyGroupMapper,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             ScheduleRepository scheduleRepository,
                             CourseMapper courseMapper,
                             CourseLookup courseLookup) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroupMapper = studyGroupMapper;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.scheduleRepository = scheduleRepository;
        this.courseMapper = courseMapper;
        this.courseLookup = courseLookup;
    }


    @Transactional
    public StudyGroupResponse create(StudyGroupCreateRequest request) {
        StudyGroup group = studyGroupMapper.toEntity(request);
        StudyGroup savedStudyGroup = studyGroupRepository.save(group);
        return studyGroupMapper.toResponse(savedStudyGroup);
    }

    private @NonNull StudyGroup getActiveGroupOrThrow(Long id) {
        return studyGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с id " + id + " не найдена."
                ));
    }

    @Transactional(readOnly = true)
    public StudyGroupResponse findById(Long id) {
        return studyGroupMapper.toResponse(getActiveGroupOrThrow(id));
    }

    @Transactional
    public StudyGroupResponse update(Long id, StudyGroupUpdateRequest request) {
        StudyGroup group = getActiveGroupOrThrow(id);
        studyGroupMapper.updateEntity(request, group);
        return studyGroupMapper.toResponse(group);
    }

    @Transactional
    public void delete(Long id) {
        StudyGroup group = getActiveGroupOrThrow(id);
        if (studentRepository.existsByGroups_IdAndDeletedFalse(id)) {
            throw new StudyGroupHasStudentsException(id);
        }
        boolean hasActiveSchedules =
                scheduleRepository.existsByGroup_IdAndDeletedFalse(id);
        if (hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя удалить группу: есть активные занятия."
            );
        }
        group.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<StudyGroupResponse> findAll(String name, Long courseId, Pageable pageable) {
        Specification<StudyGroup> specification = StudyGroupSpecification.isActive();
        if (name != null && !name.isBlank()) {
            specification = specification.and(StudyGroupSpecification.nameContains(name));
        }
        if (courseId != null) {
            specification = specification.and(StudyGroupSpecification.inCourse(courseId));
        }
        return studyGroupRepository.findAll(specification, pageable).map(studyGroupMapper::toResponse);
    }

    @Transactional
    public void addCourse(Long groupId, Long courseId) {
        StudyGroup group = getActiveGroupOrThrow(groupId);
        Course course = courseLookup.getActiveCourseOrThrow(courseId);
        group.getCourses().add(course);
        course.getGroups().add(group);
    }

    @Transactional
    public void removeCourse(Long groupId, Long courseId) {
        StudyGroup group = getActiveGroupOrThrow(groupId);
        Course course = courseLookup.getActiveCourseOrThrow(courseId);
        boolean hasActiveSchedules =
                scheduleRepository.existsByGroup_IdAndCourse_IdAndDeletedFalse(
                        groupId, courseId
                );
        if (hasActiveSchedules) {
            throw new ScheduleConflictException(
                    "Нельзя снять группу с курса: для этой пары есть активные занятия."
            );
        }
        group.getCourses().remove(course);
        course.getGroups().remove(group);
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> findCourses(Long groupId, String name, Long teacherId, Pageable pageable) {
        getActiveGroupOrThrow(groupId);
        Specification<Course> specification = CourseSpecification.isActive()
                .and(CourseSpecification.inGroup(groupId));
        if (name != null && !name.isBlank()) {
            specification = specification.and(CourseSpecification.nameContains(name));
        }
        if (teacherId != null) {
            specification = specification.and(CourseSpecification.teacherIdEquals(teacherId));
        }
        return courseRepository.findAll(specification, pageable).map(courseMapper::toResponse);
    }
}
