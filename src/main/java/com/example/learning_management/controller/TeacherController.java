package com.example.learning_management.controller;


import com.example.learning_management.dto.TeacherCreateRequest;
import com.example.learning_management.dto.TeacherResponse;
import com.example.learning_management.dto.TeacherUpdateRequest;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.mapper.TeacherMapper;
import com.example.learning_management.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;

    public TeacherController(TeacherMapper teacherMapper, TeacherService teacherService) {
        this.teacherMapper = teacherMapper;
        this.teacherService = teacherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse create(@Valid @RequestBody TeacherCreateRequest request) {
        Teacher teacher = teacherService.create(request);
        return teacherMapper.toResponse(teacher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getById(@PathVariable Long id) {
        Optional<Teacher> result = teacherService.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(teacherMapper.toResponse(result.get()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> update(@PathVariable Long id, @Valid @RequestBody TeacherUpdateRequest request) {
        Optional<Teacher> result = teacherService.update(id, request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(teacherMapper.toResponse(result.get()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = teacherService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping
    public PagedModel<TeacherResponse> findAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Teacher> teachers = teacherService.findAll(pageable);
        Page<TeacherResponse> responsePage = teachers
                .map(teacher -> teacherMapper.toResponse(teacher));
        return new PagedModel<>(responsePage);
    }
}

