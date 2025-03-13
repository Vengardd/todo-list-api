package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import com.dominik.tasktracker.TaskStatus;


public class SetInProgress implements TaskOperation {
    private final TaskDAO taskDAO;
    private final int taskId;

    public SetInProgress(TaskDAO taskDAO, int taskId) {
        this.taskDAO = taskDAO;
        this.taskId = taskId;
    }

    @Override
    public void execute() throws Exception {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskDAO.updateTask(task);
    }
}
