package com.example.learning_management.service;

import com.example.learning_management.dto.StudentCreateRequest;
import com.example.learning_management.dto.StudentUpdateRequest;
import com.example.learning_management.entity.Student;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.mapper.StudentMapper;
import com.example.learning_management.repository.StudentRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final StudyGroupRepository studyGroupRepository;

    public StudentService(StudentRepository studentRepository,
                          StudentMapper studentMapper,
                          StudyGroupRepository studyGroupRepository) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.studyGroupRepository = studyGroupRepository;
    }

    @Transactional
    public Optional<Student> create(StudentCreateRequest request) {
        List<StudyGroup> groups = studyGroupRepository.findAllById(request.groupIds());
        if (groups.isEmpty() || groups.size() != request.groupIds().size()) {
            return Optional.empty();
        }
        Student student = studentMapper.toEntity(request);
        student.getGroups().addAll(groups);
        Student savedStudent = studentRepository.save(student);
        return Optional.of(savedStudent);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Optional<Student> update(Long id, StudentUpdateRequest request) {
        Optional<Student> result = studentRepository.findById(id);
        if (result.isEmpty()) {
            return result;
        } else {
            Student updatedStudent = result.get();
            List<StudyGroup> groups = studyGroupRepository.findAllById(request.groupIds());
            if (groups.isEmpty() || groups.size() != request.groupIds().size()) {
                return Optional.empty();
            }
            studentMapper.updateEntity(request, updatedStudent);
            updatedStudent.getGroups().clear();
            updatedStudent.getGroups().addAll(groups);
        }
        return result;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Student> result = studentRepository.findById(id);
        if (result.isEmpty()) {
            return false;
        } else {
            Student student = result.get();
            studentRepository.delete(student);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
}
