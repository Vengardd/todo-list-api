package com.dominik.tasktracker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static HikariDataSource dataSource;

    public static void main(String[] args) {
        try {
            initializeDataSource();
            if (args.length >= 2) {
                String command = args[0];
                TaskDao taskDao = new TaskDao(dataSource);

                try {
                    executeCommand(command, args, taskDao);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Invalid command: {} is not recognized", command);
                    printValidCommands();
                } catch (Exception e) {
                    LOGGER.error("Error executing command", e);
                }
            } else {
                LOGGER.error("No arguments provided.");
                printValidCommands();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize application", e);
            LOGGER.error("Could not connect to database: {}", e.getMessage());
        } finally {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        }
    }

    private static void executeCommand(String commandStr, String[] args, TaskDao taskDao) throws TaskDao.TaskDaoException {
        TaskStatus command = TaskStatus.valueOf(commandStr.toUpperCase());

        try {
            switch (command) {
                case TaskStatus.ADD -> {
                    Task newTask = new Task(args[1], "MARK_IN_PROGRESS");
                    try {
                        taskDao.createTask(newTask);
                        LOGGER.info("Task added with ID {}", newTask.getId());
                    } catch (TaskDao.TaskDaoException e) {
                        LOGGER.error("Failed to add task: {}", e.getMessage());
                    }
                }

                case TaskStatus.UPDATE -> {
                    if (args.length < 3) {
                        System.out.println("Usage: update <task id> <new description>");
                        return;
                    }

                    int taskId = Integer.parseInt(args[1]);
                    Task task = taskDao.getTaskById(taskId);

                    if (task == null) {
                        System.out.println("Task not found with ID: " + taskId);
                        return;
                    }

                    task.setDescription(args[2]);
                    taskDao.updateTask(task);
                    System.out.println("Task updated successfully.");
                }

                case TaskStatus.DELETE -> {
                    if (args.length < 2) {
                        LOGGER.error("Usage: delete <task id>");
                        return;
                    }

                    int taskId = Integer.parseInt(args[1]);
                    taskDao.deleteTask(taskId);
                    LOGGER.info("Task deleted successfully.");
                }

                case TaskStatus.MARK_IN_PROGRESS -> {
                    if (args.length < 2) {
                        LOGGER.error("Usage: mark_in_progress <task id>");
                        return;
                    }

                    int taskId = Integer.parseInt(args[1]);
                    Task task = taskDao.getTaskById(taskId);

                    if (task == null) {
                        System.out.println("Task not found with ID: " + taskId);
                        return;
                    }

                    task.setStatus(TaskStatus.MARK_IN_PROGRESS);
                    taskDao.updateTask(task);
                    LOGGER.info("Task with ID {} marked as in progress.", taskId);
                }

                case TaskStatus.MARK_DONE -> {
                    if (args.length < 2) {
                        LOGGER.error("Usage: mark_done <task id>");
                        return;
                    }

                    int taskId = Integer.parseInt(args[1]);
                    Task task = taskDao.getTaskById(taskId);

                    if (task == null) {
                        LOGGER.error("Task not found with ID: {}", taskId);
                        return;
                    }

                    task.setStatus(TaskStatus.MARK_DONE);
                    taskDao.updateTask(task);
                    LOGGER.info("Task marked as done.");
                }

                case TaskStatus.LIST -> {
                    try {
                        List<Task> tasks = taskDao.getAllTasks();

                        if (tasks.isEmpty()) {
                            LOGGER.error("No tasks found.");
                            return;
                        }
                        System.out.println("Tasks:");
                        for (Task task : tasks) {
                            System.out.printf("ID: %d | %s | Status: %s | Created: %s%n",
                                    task.getId(),
                                    task.getDescription(),
                                    task.getStatus(),
                                    task.getCreatedAt().toString());
                        }
                    } catch (TaskDao.TaskDaoException e) {
                        LOGGER.error("Failed to list tasks: {}", e.getMessage());
                    }
                }

                default -> LOGGER.error("Command {} not implemented.", commandStr);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid task ID: {}", args[1]);
        }
    }

    private static void initializeDataSource() {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/cli_task_tracker";
        String username = "postgres";
        String password = "password";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
        LOGGER.info("Database connection pool initialized.");
    }

    private static void printValidCommands() {
        StringBuilder commandsMessage = new StringBuilder("Valid commands are:");
        for (TaskStatus ts : TaskStatus.values()) {
            commandsMessage.append("\n  ").append(ts);
        }
        LOGGER.error(commandsMessage.toString());
    }
}