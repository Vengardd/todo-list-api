package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import com.dominik.tasktracker.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeStatus implements TaskOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeStatus.class);
    private final TaskDAO taskDAO;
    private final int taskId;
    private final TaskStatus newStatus;

    public ChangeStatus(TaskDAO taskDAO, int taskId, TaskStatus newStatus) {
        this.taskDAO = taskDAO;
        this.taskId = taskId;
        this.newStatus = newStatus;
    }

    @Override
    public void execute() throws Exception {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            LOGGER.error("Task not found with ID: {}", taskId);
            throw new IllegalArgumentException("Task not found with ID: " + taskId);
        }

        task.setStatus(newStatus);
        taskDAO.updateTask(task);
        LOGGER.info("Task status changed to: {}", newStatus);
    }
}
