package com.example.learning_management.exception.study_group;

public class StudyGroupHasStudentsException extends RuntimeException {
    public StudyGroupHasStudentsException(Long groupId) {

        super("У группы с id " + groupId + " есть студенты. ");
    }
}
