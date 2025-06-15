package com.dominik.todolist.service;

import com.dominik.todolist.dto.TaskRequest;
import com.dominik.todolist.dto.TaskResponse;
import com.dominik.todolist.exception.TaskNotFoundException;
import com.dominik.todolist.exception.UserNotFoundException;
import com.dominik.todolist.model.AppUser;
import com.dominik.todolist.model.Task;
import com.dominik.todolist.model.TaskStatus;
import com.dominik.todolist.repository.TaskRepository;
import com.dominik.todolist.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, AppUserRepository appUserRepository) {
        this.taskRepository = taskRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Creates a new task for the given user.
     *
     * @param taskRequest DTO containing title and description
     * @param userEmail Email of the user creating the task
     * @return The created TaskResponse
     * @throws UserNotFoundException if the user does not exist
     */
    public TaskResponse createTask(TaskRequest taskRequest, String userEmail) {
        AppUser appUser = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setAppUser(appUser);

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    /**
     * Retrieves all tasks for a specific user.
     *
     * @param userEmail Email of the user
     * @return List of TaskResponse objects
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksForAppUser(String userEmail) {
        AppUser appUser = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        return taskRepository.findByAppUserId(appUser.getId())
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getAppUser().getId(),
                task.getAppUser().getEmail()
        );
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskByIdAndAppUser(Long taskId, String userEmail) {
        AppUser appUser = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getAppUser().getId().equals(appUser.getId())) {
            throw new TaskNotFoundException("Task not found for this user (or access denied)");
        }
        return mapToTaskResponse(task);
    }

    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest, String userEmail) {
        AppUser appUser = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getAppUser().getId().equals(appUser.getId())) {
            throw new TaskNotFoundException("Task not found for this user (or access denied)");
        }

        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        if (taskRequest.status() != null) {
            task.setStatus(taskRequest.status());
        }
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    public void deleteTask(Long taskId, String userEmail) {
        AppUser appUser = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getAppUser().getId().equals(appUser.getId())) {
            throw new TaskNotFoundException("Task not found for this user (or access denied)");
        }

        taskRepository.deleteById(taskId);
    }
}
