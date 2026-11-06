package com.example.learning_management.mapper;


import com.example.learning_management.dto.study_group.StudyGroupCreateRequest;
import com.example.learning_management.dto.study_group.StudyGroupResponse;
import com.example.learning_management.dto.study_group.StudyGroupUpdateRequest;
import com.example.learning_management.entity.StudyGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {
    StudyGroupResponse toResponse(StudyGroup studyGroup);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "students", ignore = true)
    StudyGroup toEntity(StudyGroupCreateRequest request);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "students", ignore = true)
    public void updateEntity(StudyGroupUpdateRequest request, @MappingTarget StudyGroup entity);
}
