package com.example.learning_management.entity;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "study_groups")
@Getter
@Setter
public class StudyGroup extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "groups")
    private Set<Student> students = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "study_group_courses",
            joinColumns = @JoinColumn(name = "study_group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();


    protected StudyGroup() {
    }

    public StudyGroup(String name) {
        this.name = name;
    }

}
