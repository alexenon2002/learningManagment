package com.example.learning_management.exception;

public class TeacherHasCoursesException extends RuntimeException {
    public TeacherHasCoursesException(Long teacherId) {
        super("У преподавателя с id " + teacherId + " есть назначенные курсы.");
    }
}