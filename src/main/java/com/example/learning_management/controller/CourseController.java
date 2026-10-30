package com.example.learning_management.controller;

import com.example.learning_management.dto.CourseCreateRequest;
import com.example.learning_management.dto.CourseResponse;
import com.example.learning_management.dto.CourseUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.service.CourseService;
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
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        Optional<Course> result = courseService.create(request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            CourseResponse response = courseMapper.toResponse(result.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        Optional<Course> result = courseService.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            CourseResponse response = courseMapper.toResponse(result.get());
            return ResponseEntity.ok().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CourseUpdateRequest request) {
        Optional<Course> result = courseService.update(id, request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(courseMapper.toResponse(result.get()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = courseService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping
    public PagedModel<CourseResponse> findAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Course> courses = courseService.findAll(pageable);
        Page<CourseResponse> responsePage = courses.map(course -> courseMapper.toResponse(course));
        return new PagedModel<>(responsePage);
    }
}