package com.example.learning_management.specification;

import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Teacher;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class CourseSpecification {
    public static Specification<Course> isActive() {
        return (root, query, cb)
                -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Course> nameContains(String name) {
        String pattern = "%" + name.strip().toLowerCase(Locale.ROOT) + "%";
        return (root,query,cb)
                -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Course> teacherIdEquals(Long teacherId) {
        return ((root, query, cb)
                -> cb.equal(root.get("teacher").get("id"), teacherId));
    }

    public static Specification<Course> inGroup(Long groupId) {
        return ((root, query, cb)
                -> cb.equal(root.join("groups").get("id"), groupId) );
    }
}
