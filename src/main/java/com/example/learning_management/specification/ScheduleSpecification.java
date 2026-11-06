package com.example.learning_management.specification;

import com.example.learning_management.entity.Course;
import com.example.learning_management.entity.Schedule;
import org.springframework.data.jpa.domain.Specification;

public class ScheduleSpecification {
    public static Specification<Schedule> isActive() {
        return (root, query, cb)
                -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Schedule> groupIdEquals(Long groupId) {
        return ((root, query, cb)
                -> cb.equal(root.get("group").get("id"), groupId));
    }

    public static Specification<Schedule> courseIdEquals(Long courseId) {
        return ((root, query, cb)
                -> cb.equal(root.get("course").get("id"), courseId));
    }

    public static Specification<Schedule> teacherIdEquals(Long teacherId) {
        return ((root, query, cb)
                -> cb.equal(root.get("teacher").get("id"), teacherId));
    }
}
