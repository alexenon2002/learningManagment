package com.example.learning_management.specification;

import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Student;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class StudentSpecification {
    public static Specification<Student> isActive() {
        return (root, query, cb)
                -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Student> nameContains(String name) {
        String pattern = "%" + name.strip().toLowerCase(Locale.ROOT) + "%";
        return (root,query,cb)
                -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Student> surnameContains(String name) {
        String pattern = "%" + name.strip().toLowerCase(Locale.ROOT) + "%";
        return (root,query,cb)
                -> cb.like(cb.lower(root.get("surname")), pattern);
    }

    public static Specification<Student> inGroup(Long groupId) {
        return ((root, query, cb)
                -> cb.equal(root.join("groups").get("id"), groupId));
    }
}
