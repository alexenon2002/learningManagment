package com.example.learning_management.exception.course;

public class CourseHasGroupsException extends RuntimeException {
    public CourseHasGroupsException(long courseId) {
        super("У курса с id " + courseId + " есть записанные группы.");
    }
}
