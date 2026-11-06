package com.example.learning_management.controller;

import com.example.learning_management.dto.course.CourseCreateRequest;
import com.example.learning_management.dto.course.CourseResponse;
import com.example.learning_management.dto.course.CourseUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.mapper.CourseMapper;
import com.example.learning_management.service.CourseService;
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
        name = "Курсы",
        description = "Управление курсами и назначением преподавателей."
)

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
    @Operation(summary = "Создать курс")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Курс создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Преподаватель не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseCreateRequest request) {
        Course result = courseService.create(request);
        CourseResponse response = courseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить курс по идентификатору")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Курс получен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Курс не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        Course course = courseService.findById(id);
        CourseResponse response = courseMapper.toResponse(course);
        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Изменить курс")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Курс изменён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Курс или преподаватель не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Нельзя сменить преподавателя,пока у курса есть занятия",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CourseUpdateRequest request) {
        Course course = courseService.update(id, request);
        return ResponseEntity.ok().body(courseMapper.toResponse(course));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить курс")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Курс мягко удалён"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Курс не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "У курса есть группы или активные занятия",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получить страницу курсов")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница курсов получена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный teacherId"
            )
    })
    public PagedModel<CourseResponse> findAll(@RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "teacherId", required = false) @Positive Long teacherId,
                                              @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Course> courses = courseService.findAll(name, teacherId, pageable);
        Page<CourseResponse> responsePage = courses.map(course -> courseMapper.toResponse(course));
        return new PagedModel<>(responsePage);
    }
}