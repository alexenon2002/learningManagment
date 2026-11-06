package com.example.learning_management.controller;


import com.example.learning_management.dto.teacher.TeacherCreateRequest;
import com.example.learning_management.dto.teacher.TeacherResponse;
import com.example.learning_management.dto.teacher.TeacherUpdateRequest;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.example.learning_management.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Преподаватели",
        description = "Создание, просмотр, изменение, удаление и поиск преподавателей."
)
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
    @Operation(summary = "Создать преподавателя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Преподаватель создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse create(@Valid @RequestBody TeacherCreateRequest request) {
        Teacher teacher = teacherService.create(request);
        return teacherMapper.toResponse(teacher);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить преподавателя по идентификатору")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Преподаватель получен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Преподаватель не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<TeacherResponse> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.findById(id);
        return ResponseEntity.ok(teacherMapper.toResponse(teacher));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Изменить преподавателя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Преподаватель изменён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Преподаватель не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<TeacherResponse> update
            (@PathVariable Long id, @Valid @RequestBody TeacherUpdateRequest request) {
        Teacher teacher = teacherService.update(id, request);
        return ResponseEntity.ok(teacherMapper.toResponse(teacher));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить преподавателя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Преподаватель мягко удалён"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Преподаватель не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "У преподавателя есть назначенные курсы",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получить страницу преподавателей")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница преподавателей получена"
            )
    })
    public PagedModel<TeacherResponse> findAll(@RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "surname", required = false) String surname,
                                               @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Teacher> teachers = teacherService.findAll(name, surname, pageable);
        Page<TeacherResponse> responsePage = teachers
                .map(teacher -> teacherMapper.toResponse(teacher));
        return new PagedModel<>(responsePage);
    }
}

