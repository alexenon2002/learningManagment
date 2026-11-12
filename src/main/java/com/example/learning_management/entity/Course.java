package com.example.learning_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course extends AuditableEntity{

    @Column(name = "name",nullable = false)
    private  String name;

    @Column(name = "description")
    private  String description;

    @ManyToOne(optional = false)
    @JoinColumn (name = "teacher_id",nullable = false)
    private  Teacher teacher;

    @ManyToMany(mappedBy = "courses")
    private Set<StudyGroup> groups = new HashSet<>();

    protected Course() {}

    public Course(String name, String description, Teacher teacher) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
    }
}
