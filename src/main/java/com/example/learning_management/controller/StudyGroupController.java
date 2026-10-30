package com.example.learning_management.controller;

import com.example.learning_management.dto.*;
        import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.mapper.StudyGroupMapper;
import com.example.learning_management.service.StudyGroupService;
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
@RequestMapping("/study_groups")
public class StudyGroupController {

    private final StudyGroupMapper studyGroupMapper;
    private final StudyGroupService studyGroupService;
    private final CourseMapper courseMapper;

    public StudyGroupController(StudyGroupMapper studyGroupMapper,
                                StudyGroupService studyGroupService,
                                CourseMapper courseMapper) {
        this.studyGroupMapper = studyGroupMapper;
        this.studyGroupService = studyGroupService;
        this.courseMapper = courseMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyGroupResponse create(@Valid @RequestBody StudyGroupCreateRequest request) {
        StudyGroup studyGroup = studyGroupService.create(request);
        return studyGroupMapper.toResponse(studyGroup);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupResponse> getById(@PathVariable Long id) {
        Optional<StudyGroup> result = studyGroupService.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(studyGroupMapper.toResponse(result.get()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyGroupResponse> update(@PathVariable Long id, @Valid @RequestBody StudyGroupUpdateRequest request) {
        Optional<StudyGroup> result = studyGroupService.update(id, request);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(studyGroupMapper.toResponse(result.get()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = studyGroupService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping
    public PagedModel<StudyGroupResponse> findAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupService.findAll(pageable);
        Page<StudyGroupResponse> responsePage = studyGroups.map(studyGroup -> studyGroupMapper.toResponse(studyGroup));
        return new PagedModel<>(responsePage);
    }

    @PutMapping("/{groupId}/courses/{courseId}")
    public ResponseEntity<Void> addCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        boolean result = studyGroupService.addCourse(groupId, courseId);
        if (!result) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{groupId}/courses/{courseId}")
    public ResponseEntity<Void> removeCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        boolean result = studyGroupService.removeCourse(groupId, courseId);
        if (!result) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{groupId}/courses")
    public ResponseEntity<PagedModel<CourseResponse>> findCourses(
            @PathVariable Long groupId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Optional<Page<Course>> result = studyGroupService.findCourses(groupId, pageable);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Page<Course> courses = result.get();
            Page<CourseResponse> responsePage = courses
                    .map(course -> courseMapper.toResponse(course));
            PagedModel<CourseResponse> response = new PagedModel<>(responsePage);
            return ResponseEntity.ok(response);
        }
    }
}