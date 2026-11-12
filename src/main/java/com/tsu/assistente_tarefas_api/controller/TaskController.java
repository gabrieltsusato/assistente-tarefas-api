package com.tsu.assistente_tarefas_api.controller;

import com.tsu.assistente_tarefas_api.dto.TaskRequest;
import com.tsu.assistente_tarefas_api.dto.TaskResponse;
import com.tsu.assistente_tarefas_api.dto.TaskUpdateRequest;
import com.tsu.assistente_tarefas_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento e consulta de tarefas")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista com todas as tarefas cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas recuperada com sucesso")
    @GetMapping
    public List<TaskResponse> listAll() {
        return taskService.findAll();
    }

    @Operation(summary = "Buscar tarefa por ID", description = "Recupera os detalhes de uma tarefa específica a partir do seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/{id}")
    public TaskResponse findById(
            @Parameter(description = "ID da tarefa a ser buscada", example = "1")
            @PathVariable Long id) {
        return taskService.findById(id);
    }

    @Operation(summary = "Criar nova tarefa", description = "Cadastra uma nova tarefa na base de dados com prioridade e categoria especificadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (erro de validação)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return taskService.save(request);
    }

    @Operation(summary = "Atualizar tarefa existente", description = "Atualiza o título, descrição, status de conclusão, prioridade, categoria ou data de vencimento de uma tarefa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @PutMapping("/{id}")
    public TaskResponse update(
            @Parameter(description = "ID da tarefa a ser atualizada", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request);
    }

    @Operation(summary = "Excluir tarefa", description = "Remove permanentemente uma tarefa do sistema pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID da tarefa a ser excluída", example = "1")
            @PathVariable Long id) {
        taskService.delete(id);
    }
}