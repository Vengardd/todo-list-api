package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.model.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;


@Component
@Scope("prototype")
public class ListAllTasks implements TaskOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListAllTasks.class);
    private final TaskDAO taskDAO;

    public ListAllTasks(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public void execute() {
        try {
            Collection<Task> tasks = taskDAO.getAllTasks();

            String result = String.format("The list contains following tasks: %s",
                    tasks.stream()
                            .map(Task::getDescription)
                            .collect(Collectors.joining(", "))
            );
            LOGGER.info(result);

            System.out.println(result);

        } catch (TaskDAO.TaskDAOException e) {
            LOGGER.error("Failed to list tasks: {}", e.getMessage());
            System.out.println("Failed to list tasks: " + e.getMessage());
        }
    }
}
