package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddTask implements TaskOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddTask.class);
    private final TaskDAO taskDAO;
    private final String description;

    public AddTask(TaskDAO taskDAO, String description) {
        this.taskDAO = taskDAO;
        this.description = description;
    }

    @Override
    public void execute() {
        try {
            Task newTask = new Task(description, "IN_PROGRESS");

            Task createdTask = taskDAO.createTask(newTask);

            LOGGER.info("Task added successfully with ID: {}", createdTask.getId());

            System.out.println("Task added successfully with ID: " + createdTask.getId());

        } catch (TaskDAO.TaskDAOException e) {
            LOGGER.error("Failed to add task: {}", e.getMessage());
            System.out.println("Failed to add task: " + e.getMessage());
        }
        System.out.println("inside AddTask class and function execute()");
    }
}
