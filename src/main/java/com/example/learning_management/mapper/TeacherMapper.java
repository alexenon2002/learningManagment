package com.example.learning_management.mapper;


import com.example.learning_management.dto.TeacherCreateRequest;
import com.example.learning_management.dto.TeacherResponse;
import com.example.learning_management.dto.TeacherUpdateRequest;
import com.example.learning_management.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherResponse toResponse(Teacher teacher);
    Teacher toEntity(TeacherCreateRequest request);

    public void updateEntity(TeacherUpdateRequest request, @MappingTarget Teacher entity);
}
