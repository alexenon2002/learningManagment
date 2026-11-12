package com.example.learning_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student extends AuditableEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    protected Student() {
    }

    public Student(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    @ManyToMany
    @JoinTable(name = "student_study_groups",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "study_group_id"))
    private Set<StudyGroup> groups = new HashSet<>();

}
