package com.example.learning_management.service;

import com.example.learning_management.dto.student.StudentCreateRequest;
import com.example.learning_management.dto.student.StudentResponse;
import com.example.learning_management.dto.student.StudentUpdateRequest;
import com.example.learning_management.entity.Student;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.mapper.StudentMapper;
import com.example.learning_management.repository.StudentRepository;
import com.example.learning_management.repository.StudyGroupRepository;
import com.example.learning_management.specification.StudentSpecification;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public StudentResponse create(@NonNull StudentCreateRequest request) {
        List<StudyGroup> groups = studyGroupRepository.findAllByIdInAndDeletedFalse(request.groupIds());
        if (groups.isEmpty() || groups.size() != request.groupIds().size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены.");
        }
        Student student = studentMapper.toEntity(request);
        student.getGroups().addAll(groups);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }

    private @NonNull Student getActiveStudentOrThrow(Long id) {
        return studentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студент с id " + id + " не найден."
                ));
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return studentMapper.toResponse(getActiveStudentOrThrow(id));
    }

    @Transactional
    public StudentResponse update(Long id, @NonNull StudentUpdateRequest request) {
        Student student = getActiveStudentOrThrow(id);
        List<StudyGroup> groups = studyGroupRepository.findAllByIdInAndDeletedFalse(request.groupIds());
        if (groups.isEmpty() || groups.size() != request.groupIds().size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены.");
        }
        studentMapper.updateEntity(request, student);
        student.getGroups().clear();
        student.getGroups().addAll(groups);
        return studentMapper.toResponse(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = getActiveStudentOrThrow(id);
        student.markDeleted();
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findAll(String name, String surname, Long groupId, Pageable pageable) {
        Specification<Student> specification = StudentSpecification.isActive();
        if (name != null && !name.isBlank()) {
            specification = specification.and(StudentSpecification.nameContains(name));
        }
        if (surname != null && !surname.isBlank()) {
            specification = specification.and(StudentSpecification.surnameContains(surname));
        }
        if (groupId != null) {
            specification = specification.and(StudentSpecification.inGroup(groupId));
        }
        return studentRepository.findAll(specification, pageable).map(studentMapper::toResponse);
    }
}
