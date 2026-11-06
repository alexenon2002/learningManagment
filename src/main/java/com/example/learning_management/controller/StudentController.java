package com.example.learning_management.controller;

import com.example.learning_management.dto.student.StudentCreateRequest;
import com.example.learning_management.dto.student.StudentResponse;
import com.example.learning_management.dto.student.StudentUpdateRequest;
import com.example.learning_management.entity.Student;
import com.example.learning_management.mapper.StudentMapper;
import com.example.learning_management.service.StudentService;
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

import java.util.Locale;

@Tag(
        name = "Студенты",
        description = "Управление студентами и их учебными группами."
)

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
    @Operation(summary = "Создать студента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Студент создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Одна или несколько групп не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<StudentResponse> create(
            @Valid @RequestBody StudentCreateRequest request) {
        Student student = studentService.create(request);
        StudentResponse response = studentMapper.toResponse(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить студента по идентификатору")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Студент получен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Студент не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<StudentResponse> findById(@PathVariable Long id) {
        Student student = studentService.findById(id);
        StudentResponse response = studentMapper.toResponse(student);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить студента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Студент изменён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Студент или одна из групп не найдены",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<StudentResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody StudentUpdateRequest request) {
        Student student = studentService.update(id, request);
        StudentResponse response = studentMapper.toResponse(student);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить студента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Студент мягко удалён"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Студент не найден",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получить страницу студентов")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница студентов получена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный groupId"
            )
    })
    public PagedModel<StudentResponse> findAll(@RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "surname", required = false) String surname,
                                               @RequestParam(name = "groupId", required = false) @Positive Long groupId,
                                               @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Student> students = studentService.findAll(name, surname, groupId, pageable);
        Page<StudentResponse> responsePage = students.map(student -> studentMapper.toResponse(student));
        return new PagedModel<>(responsePage);
    }
}
