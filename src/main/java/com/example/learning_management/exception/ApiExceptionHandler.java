package com.example.learning_management.exception;

import com.example.learning_management.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TeacherHasCoursesException.class)
    public ResponseEntity<ApiErrorResponse> handleTeacherHasCourses(
            TeacherHasCoursesException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(StudyGroupHasStudentsException.class)
    public ResponseEntity<ApiErrorResponse> handleStudyGroupHasStudents(
            StudyGroupHasStudentsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CourseHasGroupsException.class)
    public ResponseEntity<ApiErrorResponse> handleCourseHasGroupsException(
            CourseHasGroupsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(exception.getMessage()));
    }
}