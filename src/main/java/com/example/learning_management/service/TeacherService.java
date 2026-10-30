package com.example.learning_management.service;


import com.example.learning_management.dto.TeacherCreateRequest;
import com.example.learning_management.dto.TeacherUpdateRequest;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.exception.TeacherHasCoursesException;
import com.example.learning_management.mapper.TeacherMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TeacherService {

    private final TeacherMapper teacherMapper;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public TeacherService(TeacherRepository teacherRepository, TeacherMapper teacherMapper, CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Teacher create(TeacherCreateRequest request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return savedTeacher;
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Transactional
    public Optional<Teacher> update(Long id, TeacherUpdateRequest request) {
        Optional<Teacher> result = teacherRepository.findById(id);
        if (result.isEmpty()) {
            return result;
        } else {
            Teacher updatedTeacher = result.get();
            teacherMapper.updateEntity(request, updatedTeacher);
        }
        return result;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Teacher> result = teacherRepository.findById(id);
        if (result.isEmpty()) {
            return false;
        } else {
            Teacher teacher = result.get();
            if (courseRepository.existsByTeacher_Id(id)) {
                throw new TeacherHasCoursesException(id);
            }
            teacherRepository.delete(teacher);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<Teacher> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }
}