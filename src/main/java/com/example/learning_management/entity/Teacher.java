package com.example.learning_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher extends AuditableEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    protected Teacher() {
    }

    public Teacher(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
