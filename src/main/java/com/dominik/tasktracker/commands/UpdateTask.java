package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.model.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class UpdateTask implements TaskOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTask.class);
    private final TaskDAO taskDAO;
    private int taskId;
    private final String description;

    public UpdateTask(TaskDAO taskDAO, int taskId, String description) {
        this.taskDAO = taskDAO;
        this.taskId = taskId;
        this.description = description;
    }

    @Override
    public void execute() {
        try {
            Task existingTask = taskDAO.getTaskById(taskId);

            if (existingTask == null) {
                String errorMessage = String.format("Cannot update task. Task with ID %d not found.", taskId);
                LOGGER.error(errorMessage);
                System.out.println(errorMessage);
                return;
            }

            existingTask.setDescription(description);
            taskDAO.updateTask(existingTask);

            String successMessage = String.format("Successfully updated task %d with description: %s",
                    taskId, description);
            LOGGER.info(successMessage);
            System.out.println(successMessage);

        } catch (TaskDAO.TaskDAOException e) {
            String errorMessage = "Failed to update task: " + e.getMessage();
            LOGGER.error(errorMessage);
            System.out.println(errorMessage);
        }
    }
}