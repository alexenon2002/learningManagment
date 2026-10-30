package com.example.learning_management.controller;

import com.example.learning_management.dto.StudentCreateRequest;
import com.example.learning_management.dto.StudentResponse;
import com.example.learning_management.dto.StudentUpdateRequest;
import com.example.learning_management.entity.Student;
import com.example.learning_management.mapper.StudentMapper;
import com.example.learning_management.service.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(
            @Valid @RequestBody StudentCreateRequest request) {
        Optional<Student> result = studentService.create(request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            StudentResponse response = studentMapper.toResponse(result.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> findById(@PathVariable Long id) {
        Optional<Student> result = studentService.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            StudentResponse response = studentMapper.toResponse(result.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody StudentUpdateRequest request) {
        Optional<Student> result = studentService.update(id, request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            StudentResponse response = studentMapper.toResponse(result.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = studentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping
    public PagedModel<StudentResponse> findAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Student> students = studentService.findAll(pageable);
        Page<StudentResponse> responsePage = students.map(student -> studentMapper.toResponse(student));
        return new PagedModel<>(responsePage);
    }
}
