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

    /**
     * Creates a new task with the given description and IN_PROGRESS status
     *
     * @param description Description of the task
     * @return The created task with generated ID
     * @throws IllegalArgumentException if description is null or empty
     */
    public Task createTask(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Task task = new Task(description, "IN_PROGRESS");
        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks from the repository
     *
     * @return List of all tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves a task by its ID
     *
     * @param id The ID of the task to retrieve
     * @return The task with the specified ID
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    @Transactional(readOnly = true)
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    /**
     * Updates a task's status
     *
     * @param id The ID of the task to update
     * @param newStatus The new status to set
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public Task changeTaskStatus(Integer id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public Task updateTaskDescription(Integer id, String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));

        task.setDescription(newDescription);
        return taskRepository.save(task);
    }

    /**
     * Deletes a task by its ID
     *
     * @param id The ID of the task to delete
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public void deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Cannot delete. Task not found: " + id);
        }

        taskRepository.deleteById(id);
    }

    /**
     * Marks a task as "done"
     *
     * @param id The ID of the task to update
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public void markTaskDone(Integer id) {
        changeTaskStatus(id, TaskStatus.DONE);
    }
}
