package com.example.learning_management.service;


import com.example.learning_management.dto.teacher.TeacherCreateRequest;
import com.example.learning_management.dto.teacher.TeacherUpdateRequest;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.exception.ResourceNotFoundException;
import com.example.learning_management.exception.teacher.TeacherHasCoursesException;
import com.example.learning_management.mapper.TeacherMapper;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.TeacherRepository;
import com.example.learning_management.specification.TeacherSpecifications;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.learning_management.dto.teacher.TeacherResponse;

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
    public TeacherResponse create(TeacherCreateRequest request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toResponse(savedTeacher);
    }

    private @NonNull Teacher getActiveTeacherOrThrow(Long id) {
        return teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподаватель с id " + id + " не найден."
                ));
    }

    @Transactional(readOnly = true)
    public TeacherResponse findById(Long id) {
        return teacherMapper.toResponse(getActiveTeacherOrThrow(id));
    }

    @Transactional
    public TeacherResponse update(Long id, TeacherUpdateRequest request) {
        Teacher teacher = getActiveTeacherOrThrow(id);
        teacherMapper.updateEntity(request, teacher);
        return teacherMapper.toResponse(teacher);
    }

    @Transactional
    public void delete(Long id) {
        Teacher teacher = getActiveTeacherOrThrow(id);
        if (courseRepository.existsByTeacher_IdAndDeletedFalse(id)) {
            throw new TeacherHasCoursesException(id);
        }
        teacher.markDeleted();
    }


    @Transactional(readOnly = true)
    public Page<TeacherResponse> findAll(String name, String surname, Pageable pageable) {
        Specification<Teacher> specification = TeacherSpecifications.isActive();
        if (name != null && !name.isBlank()) {
            specification = specification.and(TeacherSpecifications.nameContains(name));
        }
        if (surname != null && !surname.isBlank()) {
            specification = specification.and(
                    TeacherSpecifications.surnameContains(surname)
            );
        }
        return teacherRepository.findAll(specification, pageable)
                .map(teacherMapper::toResponse);
    }
}