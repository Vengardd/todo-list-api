package com.dominik.tasktracker.service;

import com.dominik.tasktracker.exception.TaskNotFoundException;
import com.dominik.tasktracker.model.Task;
import com.dominik.tasktracker.model.TaskStatus;
import com.dominik.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Task task = new Task(description, "IN_PROGRESS");
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    public void changeTaskStatus(Integer id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));

        task.setStatus(newStatus);
        taskRepository.save(task);
    }

    // TODO - other service methods...
}
