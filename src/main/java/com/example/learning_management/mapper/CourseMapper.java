package com.example.learning_management.mapper;

import com.example.learning_management.dto.CourseCreateRequest;
import com.example.learning_management.dto.CourseResponse;
import com.example.learning_management.dto.CourseUpdateRequest;
import com.example.learning_management.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "teacherId", source = "teacher.id")
    CourseResponse toResponse(Course course);

    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseCreateRequest request);

    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    void updateEntity(CourseUpdateRequest request, @MappingTarget Course course);

}
