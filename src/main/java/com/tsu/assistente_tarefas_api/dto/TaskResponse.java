package com.tsu.assistente_tarefas_api.dto;

import com.tsu.assistente_tarefas_api.entity.Category;
import com.tsu.assistente_tarefas_api.entity.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        String title,
        String description,
        Boolean completed,
        Priority priority,
        Category category,
        LocalDateTime createdAt,
        LocalDate dueDate

) {
}
