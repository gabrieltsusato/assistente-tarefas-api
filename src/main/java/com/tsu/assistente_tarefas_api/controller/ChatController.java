package com.tsu.assistente_tarefas_api.controller;

import com.tsu.assistente_tarefas_api.dto.ChatRequest;
import com.tsu.assistente_tarefas_api.dto.ChatResponse;
import com.tsu.assistente_tarefas_api.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Assistente de IA", description = "Endpoint para interação via linguagem natural com o assistente de tarefas")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Enviar mensagem ao assistente", description = "Processa a mensagem em linguagem natural enviada pelo usuário e executa ações de consulta ou manipulação de tarefas via chamadas de ferramentas (Function Calling).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta gerada com sucesso pelo assistente"),
            @ApiResponse(responseCode = "400", description = "Mensagem inválida ou em branco")
    })
    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String response = chatService.sendMessage(request.conversationId(), request.message());
        return new ChatResponse(response);
    }
}