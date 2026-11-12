package com.tsu.assistente_tarefas_api.tools;

import com.tsu.assistente_tarefas_api.dto.TaskRequest;
import com.tsu.assistente_tarefas_api.dto.TaskResponse;
import com.tsu.assistente_tarefas_api.dto.TaskUpdateRequest;
import com.tsu.assistente_tarefas_api.entity.Category;
import com.tsu.assistente_tarefas_api.entity.Priority;
import com.tsu.assistente_tarefas_api.service.TaskService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskTools {

    private final TaskService taskService;
    private final Validator validator;

    @Tool(description = "Lista todas as tarefas cadastradas pelo usuário.")
    public List<TaskResponse> getAllTasks(){
        return taskService.findAll();
    }

    @Tool(description = "Busca uma tarefa específica pelo seu ID.")
    public TaskResponse getTaskById(
            @ToolParam(description = "ID da tarefa que será consultada")
            Long id
    ) {
        return taskService.findById(id);
    }

    @Tool(description = """
        Cria uma nova tarefa no sistema.

        Escolha a categoria e a prioridade mais adequadas com base
        no contexto fornecido pelo usuário.

        Categorias disponíveis:
        STUDY: estudos, faculdade e cursos.
        WORK: emprego, estágio e trabalho.
        PERSONAL: atividades pessoais.
        OTHER: outras atividades.

        Prioridades disponíveis:
        LOW: baixa importância ou sem urgência.
        MEDIUM: importância normal.
        HIGH: urgente ou muito importante.

        A data de vencimento é opcional e deve estar no formato YYYY-MM-DD.
        Não invente uma data caso o usuário não tenha informado uma.
        """)
    public TaskResponse createTask(

            @ToolParam(description = "Título da tarefa")
            String title,

            @ToolParam(description = "Descrição da tarefa", required = false)
            String description,

            @ToolParam(description = "Prioridade: LOW, MEDIUM ou HIGH")
            Priority priority,

            @ToolParam(description = "Categoria: STUDY, WORK, PERSONAL ou OTHER")
            Category category,

            @ToolParam(description = "Data de vencimento no formato YYYY-MM-DD", required = false)
            LocalDate dueDate

    ) {
        TaskRequest request = new TaskRequest(
                title,
                description,
                priority,
                category,
                dueDate
        );

        Set<ConstraintViolation<TaskRequest>> violations =
                validator.validate(request);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException(errors);
        }

        return taskService.save(request);
    }

    @Tool(description = """
            Altera o estado de conclusão de uma tarefa.
            Use true para marcar como concluída e false para marcar como pendente.
            """)
    public TaskResponse setTaskCompletion(

            @ToolParam(description = "ID da tarefa")
            Long id,

            @ToolParam(description = "true para concluída, false para pendente")
            Boolean completed

    ) {
        return taskService.setCompleted(id, completed);
    }

    @Tool(description = """
        Busca todas as tarefas atrasadas.
        Uma tarefa está atrasada quando não foi concluída
        e sua data de vencimento é anterior à data atual.
        """)
    public List<TaskResponse> getOverdueTasks() {
        return taskService.findOverdueTasks();
    }

    @Tool(description = """
        Busca todas as tarefas com vencimento na data atual.
        """)
    public List<TaskResponse> getTasksDueToday() {
        return taskService.findTasksDueToday();
    }

    @Tool(description = """
        Retorna a quantidade total de tarefas que ainda não foram concluídas.
        """)
    public long countPendingTasks() {
        return taskService.countPendingTasks();
    }

    @Tool(description = """
        Busca tarefas de uma categoria específica.
        Categorias disponíveis:
        STUDY, WORK, PERSONAL e OTHER.
        """)
    public List<TaskResponse> getTasksByCategory(
            @ToolParam(description = "Categoria: STUDY, WORK, PERSONAL ou OTHER")
            Category category
    ) {
        return taskService.findByCategory(category);
    }

    @Tool(description = """
        Pesquisa tarefas pelo título ou por parte dele.
        Utilize esta ferramenta quando o usuário mencionar uma tarefa pelo nome
        em vez de informar seu ID.
        A pesquisa não diferencia letras maiúsculas e minúsculas.
        """)
    public List<TaskResponse> searchTasksByTitle(
            @ToolParam(description = "Título completo ou parcial da tarefa")
            String title
    ) {
        return taskService.searchByTitle(title);
    }

    @Tool(description = "Atualiza os dados de uma tarefa existente pelo seu ID.")
    public TaskResponse updateTask(
            @ToolParam(description = "ID da tarefa") Long id,
            @ToolParam(description = "Novo título da tarefa") String title,
            @ToolParam(description = "Nova descrição", required = false) String description,
            @ToolParam(description = "Status de conclusão", required = false) Boolean completed,
            @ToolParam(description = "Prioridade: LOW, MEDIUM ou HIGH") Priority priority,
            @ToolParam(description = "Categoria: STUDY, WORK, PERSONAL ou OTHER") Category category,
            @ToolParam(description = "Data de vencimento YYYY-MM-DD", required = false) LocalDate dueDate
    ) {
        TaskUpdateRequest request = new TaskUpdateRequest(title, description, completed, priority, category, dueDate);
        Set<ConstraintViolation<TaskUpdateRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(errors);
        }
        return taskService.update(id, request);
    }

    @Tool(description = "Exclui uma tarefa do sistema pelo seu ID.")
    public String deleteTask(
            @ToolParam(description = "ID da tarefa a ser excluída") Long id
    ) {
        taskService.delete(id);
        return "Tarefa com ID " + id + " excluída com sucesso.";
    }


}
