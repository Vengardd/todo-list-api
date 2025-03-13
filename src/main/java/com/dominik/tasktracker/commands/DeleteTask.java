package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTask implements TaskOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTask.class);
    private final TaskDAO taskDAO;
    private int taskId;

    public DeleteTask(TaskDAO taskDAO, int taskId) {
        this.taskDAO = taskDAO;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        try {
            Task taskToDelete = taskDAO.getTaskById(taskId);

            if (taskToDelete == null) {
                String errorMessage = String.format("Cannot delete task. Task with ID %d not found.", taskId);
                LOGGER.error(errorMessage);
                System.out.println(errorMessage);
                return;
            }

            taskDAO.deleteTask(taskId);

            String successMessage = String.format("Successfully deleted task with ID: %d", taskId);
            LOGGER.info(successMessage);
            System.out.println(successMessage);

        } catch (TaskDAO.TaskDAOException e) {
            String errorMessage = "Failed to delete task: " + e.getMessage();
            LOGGER.error(errorMessage);
            System.out.println(errorMessage);
        }
    }
}