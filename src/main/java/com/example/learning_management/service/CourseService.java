package com.example.learning_management.service;


import com.example.learning_management.dto.CourseCreateRequest;
import com.example.learning_management.dto.CourseResponse;
import com.example.learning_management.dto.CourseUpdateRequest;
import com.example.learning_management.dto.TeacherResponse;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.exception.CourseHasGroupsException;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import com.example.learning_management.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;
    private final StudyGroupRepository studyGroupRepository;

    public CourseService(CourseRepository courseRepository,
                         TeacherRepository teacherRepository,
                         CourseMapper mapper,
                         StudyGroupRepository studyGroupRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.courseMapper = mapper;
        this.studyGroupRepository = studyGroupRepository;
    }

    @Transactional
    public Optional<Course> create(CourseCreateRequest request) {
        Optional<Teacher> teacherResult =
                teacherRepository.findById(request.teacherId());
        if (teacherResult.isEmpty()) {
            return Optional.empty();
        }
        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacherResult.get());
        Course savedCourse = courseRepository.save(course);
        return Optional.of(savedCourse);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Optional<Course> update(Long id, CourseUpdateRequest request) {
        Optional<Course> courseResult = courseRepository.findById(id);
        if (courseResult.isEmpty()) {
            return Optional.empty();
        }
        Optional<Teacher> teacherResult = teacherRepository.findById(request.teacherId());
        if (teacherResult.isEmpty()) {
            return Optional.empty();
        }
        Course course = courseResult.get();
        courseMapper.updateEntity(request, course);
        course.setTeacher(teacherResult.get());
        return courseResult;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Course> result = courseRepository.findById(id);
        if (result.isEmpty()) {
            return false;
        } else {
            Course course = result.get();
            if (studyGroupRepository.existsByCourses_Id(id)) {
                throw new CourseHasGroupsException(id);
            }
            courseRepository.delete(course);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

}