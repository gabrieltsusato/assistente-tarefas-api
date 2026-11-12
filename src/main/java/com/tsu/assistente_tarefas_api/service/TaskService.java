package com.tsu.assistente_tarefas_api.service;

import com.tsu.assistente_tarefas_api.dto.TaskRequest;
import com.tsu.assistente_tarefas_api.dto.TaskResponse;
import com.tsu.assistente_tarefas_api.dto.TaskUpdateRequest;
import com.tsu.assistente_tarefas_api.entity.Category;
import com.tsu.assistente_tarefas_api.entity.Task;
import com.tsu.assistente_tarefas_api.exception.TaskNotFoundException;
import com.tsu.assistente_tarefas_api.mapper.TaskMapper;
import com.tsu.assistente_tarefas_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada"));

        return taskMapper.toResponse(task);
    }

    public TaskResponse save(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse update(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada"));

        taskMapper.updateEntity(task, request);

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada"));

        taskRepository.delete(task);
    }

    public TaskResponse setCompleted(Long id, boolean completed) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada"));

        task.setCompleted(completed);

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    public List<TaskResponse> findOverdueTasks() {
        return taskRepository.findByCompletedFalseAndDueDateBefore(LocalDate.now())
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> findTasksDueToday() {
        return taskRepository.findByDueDate(LocalDate.now())
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public long countPendingTasks() {
        return taskRepository.countByCompletedFalse();
    }

    public List<TaskResponse> findByCategory(Category category) {
        return taskRepository.findByCategory(category)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> searchByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }
}