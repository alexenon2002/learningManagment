package com.example.learning_management.mapper;


import com.example.learning_management.dto.student.StudentCreateRequest;
import com.example.learning_management.dto.student.StudentResponse;
import com.example.learning_management.dto.student.StudentUpdateRequest;
import com.example.learning_management.entity.Student;
import com.example.learning_management.entity.StudyGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "groups", ignore = true)
    Student toEntity(StudentCreateRequest request);

    @Mapping(target = "groupIds", source = "groups")
    StudentResponse toResponse(Student student);

    @Mapping(target = "groups", ignore = true)
    public void updateEntity(StudentUpdateRequest request, @MappingTarget Student student);

    default Long groupToId(StudyGroup group) {
        if (group == null) {
            return null;
        } else {
            return group.getId();
        }
    }
}
