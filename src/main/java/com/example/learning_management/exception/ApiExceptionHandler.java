package com.example.learning_management.exception;

import com.example.learning_management.dto.ApiErrorResponse;
import com.example.learning_management.exception.course.CourseHasGroupsException;
import com.example.learning_management.exception.schedule.InvalidScheduleTimeException;
import com.example.learning_management.exception.schedule.ScheduleConflictException;
import com.example.learning_management.exception.study_group.StudyGroupHasStudentsException;
import com.example.learning_management.exception.teacher.TeacherHasCoursesException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TeacherHasCoursesException.class)
    public ResponseEntity<ApiErrorResponse> handleTeacherHasCourses(
            @NonNull TeacherHasCoursesException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(StudyGroupHasStudentsException.class)
    public ResponseEntity<ApiErrorResponse> handleStudyGroupHasStudents(
            @NonNull StudyGroupHasStudentsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CourseHasGroupsException.class)
    public ResponseEntity<ApiErrorResponse> handleCourseHasGroupsException(
            @NonNull CourseHasGroupsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            @NonNull ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidScheduleTimeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidScheduleTime(
            @NonNull InvalidScheduleTimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleScheduleConflict(
            @NonNull ScheduleConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(exception.getMessage()));
    }
}