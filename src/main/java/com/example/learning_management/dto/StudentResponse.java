package com.example.learning_management.dto;

import java.util.Set;

public record StudentResponse(Long id, String name, String surname, Set<Long> groupIds) {
}
