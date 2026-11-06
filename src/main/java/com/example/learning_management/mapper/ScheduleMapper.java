package com.example.learning_management.mapper;

import com.example.learning_management.dto.schedule.ScheduleCreateRequest;
import com.example.learning_management.dto.schedule.ScheduleResponse;
import com.example.learning_management.dto.schedule.ScheduleUpdateRequest;
import com.example.learning_management.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    Schedule toEntity(ScheduleCreateRequest request);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "teacherId", source = "teacher.id")
    ScheduleResponse toResponse(Schedule schedule);

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntity(ScheduleUpdateRequest request, @MappingTarget Schedule schedule);
}
