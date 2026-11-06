package com.example.learning_management.controller;

import com.example.learning_management.dto.course.CourseResponse;
import com.example.learning_management.dto.study_group.StudyGroupCreateRequest;
import com.example.learning_management.dto.study_group.StudyGroupResponse;
import com.example.learning_management.dto.study_group.StudyGroupUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.StudyGroup;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.mapper.StudyGroupMapper;
import com.example.learning_management.service.StudyGroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.example.learning_management.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Учебные группы",
        description = "Управление группами и записью групп на курсы."
)

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
    @Operation(summary = "Создать группу")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Группа создана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public StudyGroupResponse create(@Valid @RequestBody StudyGroupCreateRequest request) {
        StudyGroup group = studyGroupService.create(request);
        return studyGroupMapper.toResponse(group);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить группу по идентификатору")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Группа получена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа не найдена",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<StudyGroupResponse> getById(@PathVariable Long id) {
        StudyGroup group = studyGroupService.findById(id);
        return ResponseEntity.ok(studyGroupMapper.toResponse(group));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить группу")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Группа изменена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа не найдена",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<StudyGroupResponse> update(@PathVariable Long id, @Valid @RequestBody StudyGroupUpdateRequest request) {
        StudyGroup group = studyGroupService.update(id, request);
        return ResponseEntity.ok(studyGroupMapper.toResponse(group));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить группу")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Группа мягко удалена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа не найдена",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "У группы есть студенты или активные занятия",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studyGroupService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получить страницу учебных групп")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница учебных групп получена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный courseId"
            )
    })
    public PagedModel<StudyGroupResponse> findAll(@RequestParam(name = "name", required = false) String name,
                                                  @RequestParam(name = "courseId", required = false) @Positive Long courseId,
                                                  @PageableDefault(size = 10, sort = "id")
                                                  Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupService.findAll(name, courseId, pageable);
        Page<StudyGroupResponse> responsePage = studyGroups
                .map(studyGroup -> studyGroupMapper.toResponse(studyGroup));
        return new PagedModel<>(responsePage);
    }

    @PutMapping("/{groupId}/courses/{courseId}")
    @Operation(summary = "Записать группу на курс")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Группа записана на курс"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа или курс не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        studyGroupService.addCourse(groupId, courseId);
    }

    @DeleteMapping("/{groupId}/courses/{courseId}")
    @Operation(summary = "Снять группу с курса")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Группа снята с курса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа или курс не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Для пары есть активные занятия",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        studyGroupService.removeCourse(groupId, courseId);
    }

    @GetMapping("/{groupId}/courses")
    @Operation(summary = "Получить курсы учебной группы")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница курсов группы получена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный teacherId"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Группа не найдена",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<PagedModel<CourseResponse>> findCourses(
            @PathVariable Long groupId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "teacherId", required = false) @Positive Long teacherId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Course> courses = studyGroupService.findCourses(groupId, name, teacherId, pageable);
        Page<CourseResponse> responsePage = courses
                .map(course -> courseMapper.toResponse(course));
        PagedModel<CourseResponse> response = new PagedModel<>(responsePage);
        return ResponseEntity.ok(response);
    }
}