package com.example.learning_management.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherCreateRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String surname) {
}
