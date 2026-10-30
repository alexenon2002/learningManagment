package com.example.learning_management.service;

import com.example.learning_management.dto.StudyGroupCreateRequest;
import com.example.learning_management.dto.StudyGroupUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.exception.StudyGroupHasStudentsException;
import com.example.learning_management.mapper.StudyGroupMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.StudentRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMapper studyGroupMapper;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudyGroupService(StudyGroupRepository studyGroupRepository,
                             StudyGroupMapper studyGroupMapper,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroupMapper = studyGroupMapper;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public StudyGroup create(StudyGroupCreateRequest request) {
        StudyGroup studyGroup = studyGroupMapper.toEntity(request);
        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);
        return savedStudyGroup;
    }

    public Optional<StudyGroup> findById(Long id) {
        return studyGroupRepository.findById(id);
    }

    @Transactional
    public Optional<StudyGroup> update(Long id, StudyGroupUpdateRequest request) {
        Optional<StudyGroup> result = studyGroupRepository.findById(id);
        if (result.isEmpty()) {
            return result;
        } else {
            StudyGroup updatedStudyGroup = result.get();
            studyGroupMapper.updateEntity(request, updatedStudyGroup);
        }
        return result;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<StudyGroup> result = studyGroupRepository.findById(id);
        if (result.isEmpty()) {
            return false;
        } else {
            StudyGroup studyGroup = result.get();
            if (studentRepository.existsByGroups_Id(id)) {
                throw new StudyGroupHasStudentsException(id);
            }
            studyGroupRepository.delete(studyGroup);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<StudyGroup> findAll(Pageable pageable) {
        return studyGroupRepository.findAll(pageable);
    }

    @Transactional
    public boolean addCourse(Long groupId, Long courseId) {
        Optional<StudyGroup> groupResult = studyGroupRepository.findById(groupId);
        if (groupResult.isEmpty()) {
            return false;
        }
        Optional<Course> courseResult = courseRepository.findById(courseId);
        if (courseResult.isEmpty()) {
            return false;
        }
        StudyGroup group = groupResult.get();
        Course course = courseResult.get();
        group.getCourses().add(course);
        course.getGroups().add(group);
        return true;
    }

    @Transactional
    public boolean removeCourse(Long groupId, Long courseId) {
        Optional<StudyGroup> groupResult = studyGroupRepository.findById(groupId);
        if (groupResult.isEmpty()) {
            return false;
        }
        Optional<Course> courseResult = courseRepository.findById(courseId);
        if (courseResult.isEmpty()) {
            return false;
        }
        StudyGroup group = groupResult.get();
        Course course = courseResult.get();
        group.getCourses().remove(course);
        course.getGroups().remove(group);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Page<Course>> findCourses(Long groupId, Pageable pageable) {
        boolean groupExists = studyGroupRepository.existsById(groupId);
        if (!groupExists) {
            return Optional.empty();
        } else {
            Page<Course> courses = courseRepository.findByGroups_Id(groupId, pageable);
            return Optional.of(courses);
        }
    }
}
