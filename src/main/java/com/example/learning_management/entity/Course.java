package com.example.learning_management.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
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

    public Set<StudyGroup> getGroups() {
        return groups;
    }

    protected Course() {}

    public Course(String name, String description, Teacher teacher) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
