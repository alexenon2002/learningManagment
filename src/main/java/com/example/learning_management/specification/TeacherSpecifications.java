package com.example.learning_management.specification;

import com.example.learning_management.entity.Teacher;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class TeacherSpecifications {
    public static Specification<Teacher> isActive() {
        return (root, query, cb)
                -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Teacher> nameContains(String name) {
        String pattern = "%" + name.strip().toLowerCase(Locale.ROOT) + "%";
        return (root,query,cb)
                -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Teacher> surnameContains(String surname) {
        String pattern = "%" + surname.strip().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("surname")), pattern);
    }
}
