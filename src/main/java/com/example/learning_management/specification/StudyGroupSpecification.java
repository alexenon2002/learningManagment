package com.example.learning_management.specification;

import com.example.learning_management.entity.StudyGroup;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class StudyGroupSpecification {

    public static Specification<StudyGroup> isActive() {
        return ((root, query, cb)
                -> cb.isNull(root.get("deletedAt")));
    }

    public static Specification<StudyGroup> nameContains(String name) {
        String pattern = "%" + name.strip().toLowerCase(Locale.ROOT) + "%";
        return ((root, query, cb)
                -> cb.like(cb.lower(root.get("name")), pattern));
    }

    public static Specification<StudyGroup> inCourse(Long courseId) {
        return  ((root, query, cb)
                -> cb.equal(root.join("courses").get("id"), courseId));
    }
}
