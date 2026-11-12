package com.example.learning_management.controller;


import com.example.learning_management.dto.schedule.ScheduleCreateRequest;
import com.example.learning_management.dto.schedule.ScheduleResponse;
import com.example.learning_management.dto.schedule.ScheduleUpdateRequest;
import com.example.learning_management.service.ScheduleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
        name = "Расписание",
        description = "Создание занятий, просмотр расписания групп и преподавателей."
)

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @Operation(summary = "Создать занятие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Занятие создано"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные занятия"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдена группа или курс",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Группа не записана на указанный курс",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse create(@Valid @RequestBody ScheduleCreateRequest request) {
        return scheduleService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить занятие по идентификатору")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Занятие получено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Занятие не найдено",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить занятие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Занятие изменено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные поля запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Занятие, группа или курс не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Группа не записана на указанный курс",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ScheduleResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ScheduleUpdateRequest request) {
        return ResponseEntity.ok().body(scheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить занятие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Занятие мягко удалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Занятие не найдено",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получить расписание с фильтрами")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Расписание получено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные поля запроса"
            )
    })
    public PagedModel<ScheduleResponse> findAll(
            @RequestParam(name = "groupId", required = false) @Positive Long groupId,
            @RequestParam(name = "courseId", required = false) @Positive Long courseId,
            @RequestParam(name = "teacherId", required = false) @Positive Long teacherId,
            @PageableDefault(size = 10, sort = {"startsAt", "id"}) Pageable pageable) {
        return new PagedModel<>(scheduleService.findAll(groupId, courseId, teacherId, pageable));
    }
}
