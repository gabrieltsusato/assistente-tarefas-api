package com.tsu.assistente_tarefas_api.mapper;

import com.tsu.assistente_tarefas_api.dto.TaskRequest;
import com.tsu.assistente_tarefas_api.dto.TaskResponse;
import com.tsu.assistente_tarefas_api.dto.TaskUpdateRequest;
import com.tsu.assistente_tarefas_api.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request){
        Task task = new Task();

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setCategory(request.category());
        task.setDueDate(request.dueDate());
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        return task;
    }

    public void updateEntity(Task task, TaskUpdateRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        task.setPriority(request.priority());
        task.setCategory(request.category());
        task.setDueDate(request.dueDate());
    }

    public TaskResponse toResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCompleted(),
                task.getPriority(),
                task.getCategory(),
                task.getCreatedAt(),
                task.getDueDate()
        );
    }





}
