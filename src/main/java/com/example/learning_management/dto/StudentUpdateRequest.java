package com.example.learning_management.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record StudentUpdateRequest(@NotBlank @Size(max = 255) String name,
                                   @NotBlank @Size(max = 255) String surname,
                                   @NotEmpty Set<@NotNull @Positive Long> groupIds) {
}
