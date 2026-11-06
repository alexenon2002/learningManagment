package com.example.learning_management.exception.teacher;

public class TeacherHasCoursesException extends RuntimeException {
    public TeacherHasCoursesException(Long teacherId) {
        super("У преподавателя с id " + teacherId + " есть назначенные курсы.");
    }
}