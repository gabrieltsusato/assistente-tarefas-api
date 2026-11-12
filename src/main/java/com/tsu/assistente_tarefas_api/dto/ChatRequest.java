package com.tsu.assistente_tarefas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "O ID da conversa é obrigatório")
        String conversationId,

        @NotBlank(message = "A mensagem não pode estar vazia")
        @Size(max = 2000, message = "A mensagem deve ter no máximo 2000 caracteres")
        String message
) {


}
