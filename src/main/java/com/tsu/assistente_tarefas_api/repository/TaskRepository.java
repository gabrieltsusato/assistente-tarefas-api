package com.tsu.assistente_tarefas_api.repository;

import com.tsu.assistente_tarefas_api.entity.Category;
import com.tsu.assistente_tarefas_api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedFalseAndDueDateBefore(LocalDate date);

    List<Task> findByDueDate(LocalDate date);

    long countByCompletedFalse();

    List<Task> findByCategory(Category category);

    List<Task> findByTitleContainingIgnoreCase(String title);
}
