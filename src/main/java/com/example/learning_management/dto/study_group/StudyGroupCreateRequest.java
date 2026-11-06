package com.example.learning_management.dto.study_group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyGroupCreateRequest(@NotBlank @Size(max = 255) String name) {
}
