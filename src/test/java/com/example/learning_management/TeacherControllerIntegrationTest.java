package com.example.learning_management;

import com.example.learning_management.dto.teacher.TeacherCreateRequest;
import com.example.learning_management.dto.teacher.TeacherResponse;
import com.example.learning_management.dto.teacher.TeacherUpdateRequest;
import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Teacher;
import com.example.learning_management.repository.CourseRepository;
import com.example.learning_management.repository.TeacherRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TeacherControllerIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    record TeacherPageResponse(List<TeacherResponse> content, PagedModel.PageMetadata page) {
    }

    record PageMetaData(int size, int number, long totalPages, int totalElements) {
    }

    @Autowired
    private TeacherRepository teacherRepository;

    private Long teacherId;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        Teacher teacher = new Teacher("Анна", "Петрова");
        Teacher savedTeacher = teacherRepository.save(teacher);
        teacherId = savedTeacher.getId();
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void getTeacherById_ReturnsSavedTeacher() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        ResponseEntity<TeacherResponse> response =
                restTemplate.getForEntity(url, TeacherResponse.class, teacherId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(teacherId, body.id());
        assertEquals("Анна", body.name());
        assertEquals("Петрова", body.surname());
    }

    @Test
    void getTeacherById_ReturnsNotFound_WhenTeacherDoesNotExist() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        Long missingId = Long.MAX_VALUE;
        HttpClientErrorException.NotFound exception = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.getForEntity(url, TeacherResponse.class, missingId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteTeacher_MarksTeacherAsDeleted() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, teacherId);
        Teacher result = teacherRepository.findById(teacherId).orElseThrow();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(result.isDeleted());
        assertNotNull(result.getDeletedAt());
        HttpClientErrorException.NotFound exception = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.getForEntity(url, TeacherResponse.class, teacherId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createTeacher_ReturnsCreatedAndSavesTeacher() {
        String url = "http://localhost:" + port + "/teachers";
        TeacherCreateRequest request = new TeacherCreateRequest("Иван", "Сидоров");
        ResponseEntity<TeacherResponse> response =
                restTemplate.postForEntity(url, request, TeacherResponse.class);
        TeacherResponse body = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.id());
        assertNotEquals(teacherId, body.id());
        assertEquals("Иван", body.name());
        assertEquals("Сидоров", body.surname());
        Teacher savedTeacher = teacherRepository.findById(body.id())
                .orElseThrow(
                        () -> new AssertionError("Преподаватель не сохранён в БД")
                );
        assertEquals(body.name(), savedTeacher.getName());
        assertEquals(body.surname(), savedTeacher.getSurname());
    }


    static @NonNull Stream<TeacherCreateRequest> invalidCreateRequests() {
        return Stream.of(
                new TeacherCreateRequest(null, "Сидоров"),
                new TeacherCreateRequest("  ", "Сидоров"),
                new TeacherCreateRequest("A".repeat(256), "Сидоров"),
                new TeacherCreateRequest("Иван", null),
                new TeacherCreateRequest("Иван", "  "),
                new TeacherCreateRequest("Иван", "A".repeat(256))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    void createTeacher_ReturnsBadRequest_WhenRequestIsInvalid(TeacherCreateRequest request) {
        String url = "http://localhost:" + port + "/teachers";
        long countBefore = teacherRepository.count();
        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.postForEntity(url, request, TeacherResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(countBefore, teacherRepository.count());
    }

    @Test
    void getTeacherById_ReturnsBadRequest_WhenIdIsNotNumeric() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        HttpClientErrorException.BadRequest exception = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> restTemplate.getForEntity(url, TeacherResponse.class, "abc")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void updateTeacher_ReturnsUpdatedTeacherAndPersistsChanges() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        TeacherUpdateRequest request = new TeacherUpdateRequest("Мария", "Иванова");
        ResponseEntity<TeacherResponse> response = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(request),
                        TeacherResponse.class, teacherId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(teacherId, body.id());
        assertEquals("Мария", body.name());
        assertEquals("Иванова", body.surname());
        Teacher savedTeacher = teacherRepository.findById(body.id())
                .orElseThrow(
                        () -> new AssertionError("Преподаватель не сохранён в БД")
                );
        assertEquals(body.name(), savedTeacher.getName());
        assertEquals(body.surname(), savedTeacher.getSurname());
    }

    static Stream<TeacherUpdateRequest> invalidUpdateRequests() {
        return invalidCreateRequests()
                .map(request
                        -> new TeacherUpdateRequest(request.name(), request.surname()));
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateRequests")
    void updateTeacher_ReturnsBadRequest_WhenRequestIsInvalid(TeacherUpdateRequest request) {
        String url = "http://localhost:" + port + "/teachers/{id}";
        HttpClientErrorException.BadRequest exception = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> restTemplate.exchange(
                        url, HttpMethod.PUT, new HttpEntity<>(request), String.class, teacherId)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Teacher savedTeacher = teacherRepository.findById(teacherId).orElseThrow();
        assertEquals("Анна", savedTeacher.getName());
        assertEquals("Петрова", savedTeacher.getSurname());
    }

    @Test
    void updateTeacher_ReturnsNotFound_WhenTeacherDoesNotExists() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        TeacherUpdateRequest request = new TeacherUpdateRequest("Мария", "Иванова");
        HttpClientErrorException.NotFound exception = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.exchange(
                        url, HttpMethod.PUT, new HttpEntity<>(request), String.class, Long.MAX_VALUE)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateTeacher_ReturnsNotFound_WhenTeacherIsDeleted() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        teacher.markDeleted();
        teacherRepository.save(teacher);
        TeacherUpdateRequest request = new TeacherUpdateRequest("Мария", "Иванова");
        HttpClientErrorException.NotFound exception = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.exchange(
                        url, HttpMethod.PUT, new HttpEntity<>(request), String.class, teacherId)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Teacher savedTeacher = teacherRepository.findById(teacherId).orElseThrow();
        assertTrue(savedTeacher.isDeleted());
        assertEquals("Анна", savedTeacher.getName());
        assertEquals("Петрова", savedTeacher.getSurname());
    }

    @Test
    void deleteTeacher_ReturnsNotFound_WhenTeacherDoesNotExists() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        HttpClientErrorException.NotFound exception = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.delete(url, Long.MAX_VALUE)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteTeacher_ReturnsNotFound_WhenAlreadyDeleted() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        ResponseEntity<Void> firstResponse =
                restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, teacherId);
        assertEquals(HttpStatus.NO_CONTENT, firstResponse.getStatusCode());

        HttpClientErrorException.NotFound secondResponse = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> restTemplate.delete(url, teacherId)
        );
        assertEquals(HttpStatus.NOT_FOUND, secondResponse.getStatusCode());
    }

    @Test
    void deleteTeacher_ReturnsConflict_WhenTeacherHasActiveCourse() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = new Course("Java", "Вводный курс", teacher);
        courseRepository.save(course);
        HttpClientErrorException.Conflict exception = assertThrows(
                HttpClientErrorException.Conflict.class,
                () -> restTemplate.delete(url, teacherId)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Teacher teacherAfterRequest = teacherRepository.findById(teacherId).orElseThrow();
        assertFalse(teacherAfterRequest.isDeleted());
    }

    @Test
    void deleteTeacher_ReturnsNoContent_WhenTeacherHasOnlyDeletedCourses() {
        String url = "http://localhost:" + port + "/teachers/{id}";
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = new Course("Java", "Вводный курс", teacher);
        course.markDeleted();
        courseRepository.save(course);
        ResponseEntity<Void> response = restTemplate
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, teacherId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Teacher teacherAfterRequest = teacherRepository.findById(teacherId).orElseThrow();
        assertTrue(teacherAfterRequest.isDeleted());
    }

    @Test
    void getTeachers_ReturnsRequestedPage() {
        String url = "http://localhost:" + port
                + "/teachers?page={page}&size=1&sort=id,asc";
        Teacher secondTeacher = teacherRepository.save(new Teacher("Иван", "Сидоров"));
        Long secondTeacherId = secondTeacher.getId();
        ResponseEntity<TeacherPageResponse> firstResponse = restTemplate
                .getForEntity(url, TeacherPageResponse.class, 0);
        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());

        TeacherPageResponse firstPage = firstResponse.getBody();
        assertNotNull(firstPage);
        assertEquals(1, firstPage.content().size());
        assertEquals(teacherId, firstPage.content().getFirst().id());
        assertEquals(0, firstPage.page().number());
        assertEquals(1, firstPage.page().size());
        assertEquals(2L, firstPage.page().totalElements());
        assertEquals(2, firstPage.page().totalPages());


        ResponseEntity<TeacherPageResponse> secondResponse = restTemplate
                .getForEntity(url, TeacherPageResponse.class, 1);
        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());
        TeacherPageResponse secondPage = secondResponse.getBody();
        assertNotNull(secondPage);
        assertEquals(1, secondPage.content().size());
        assertEquals(secondTeacherId, secondPage.content().getFirst().id());
        assertEquals(1, secondPage.page().number());
        assertEquals(1, secondPage.page().size());
        assertEquals(2L, secondPage.page().totalElements());
        assertEquals(2, secondPage.page().totalPages());
    }

    @Test
    void getTeachers_WithFiltersOfName() {
        Teacher firstTeacher = teacherRepository.save(new Teacher("Анна", "Сидорова"));
        Long firstTeacherId = firstTeacher.getId();
        Teacher secondTeacher = teacherRepository.save(new Teacher("Мария", "Петрова"));
        Long secondTeacherId = secondTeacher.getId();

        String url = "http://localhost:" + port + "/teachers?name={name}&sort=id,asc";
        ResponseEntity<TeacherPageResponse> response = restTemplate
                .getForEntity(url, TeacherPageResponse.class, "нн");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherPageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(List.of(teacherId, firstTeacherId),
                body.content.stream().map(TeacherResponse::id).toList());
        assertEquals(2, body.page.totalElements());
    }

    @Test
    void getTeachers_WithFiltersOfSurname() {
        Teacher firstTeacher = teacherRepository.save(new Teacher("Анна", "Сидорова"));
        Long firstTeacherId = firstTeacher.getId();
        Teacher secondTeacher = teacherRepository.save(new Teacher("Мария", "Петрова"));
        Long secondTeacherId = secondTeacher.getId();

        String url = "http://localhost:" + port + "/teachers?surname={surname}&sort=id,asc";
        ResponseEntity<TeacherPageResponse> response = restTemplate
                .getForEntity(url, TeacherPageResponse.class, "тров");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherPageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(List.of(teacherId, secondTeacherId),
                body.content.stream().map(TeacherResponse::id).toList());
        assertEquals(2, body.page.totalElements());
    }

    @Test
    void getTeachers_WithFiltersOfNameAndSurname() {
        Teacher firstTeacher = teacherRepository.save(new Teacher("Анна", "Сидорова"));
        Long firstTeacherId = firstTeacher.getId();
        Teacher secondTeacher = teacherRepository.save(new Teacher("Мария", "Петрова"));
        Long secondTeacherId = secondTeacher.getId();

        String url = "http://localhost:" + port + "/teachers?name={name}&surname={surname}&sort=id,asc";
        ResponseEntity<TeacherPageResponse> response = restTemplate
                .getForEntity(url, TeacherPageResponse.class, "нн", "тров");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherPageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(List.of(teacherId),
                body.content.stream().map(TeacherResponse::id).toList());
        assertEquals(1, body.page.totalElements());
    }

    @Test
    void getTeachers_ReturnsEmptyPage_WhenNoTeacherMatches() {
        String url = "http://localhost:" + port + "/teachers?name={name}&sort=id,asc";
        ResponseEntity<TeacherPageResponse> response = restTemplate
                .getForEntity(url, TeacherPageResponse.class, "Кирилл");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherPageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(List.of(), body.content());
        assertEquals(0, body.page.totalElements());
        assertEquals(0, body.page.totalPages());
    }

    @Test
    void getTeachers_ExcludesSoftDeletedTeachers() {
        Teacher deletedTeacher = new Teacher("Анна", "Сидорова");
        deletedTeacher.markDeleted();
        teacherRepository.save(deletedTeacher);
        String url = "http://localhost:" + port + "/teachers?name={name}&sort=id,asc";

        ResponseEntity<TeacherPageResponse> response = restTemplate
                .getForEntity(url, TeacherPageResponse.class, "нн");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TeacherPageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(List.of(teacherId),
                body.content.stream().map(TeacherResponse::id).toList());
        assertEquals(1, body.page.totalElements());
    }
}
