package com.tsu.assistente_tarefas_api.dto;

import com.tsu.assistente_tarefas_api.entity.Category;
import com.tsu.assistente_tarefas_api.entity.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String title,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        Boolean completed,

        @NotNull(message = "A prioridade é obrigatória")
        Priority priority,

        @NotNull(message = "A categoria é obrigatória")
        Category category,

        @FutureOrPresent(message = "A data de vencimento não pode estar no passado")
        LocalDate dueDate
) {
}
