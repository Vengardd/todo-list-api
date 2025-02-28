package com.dominik.tasktracker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;


public class DatabaseTest {
    public static void main(String[] args) throws Exception {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/cli_task_tracker";
        String username = "postgres";
        String password = "password";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        HikariDataSource dataSource = new HikariDataSource(config);

        TaskDao taskDao = new TaskDao(dataSource);

        Task task1 = new Task("Learn HikariCP", "MARK_IN_PROGRESS");
        Task createdTask1 = taskDao.createTask(task1);
        System.out.println("Created Task 1: " + createdTask1.getId());

        Task task2 = new Task("Write Unit Tests", "MARK_IN_PROGRESS");
        Task createdTask2 = taskDao.createTask(task2);
        System.out.println("Created Task 2: " + createdTask2.getId());

        List<Task> allTasks = taskDao.getAllTasks();
        System.out.println("All Tasks:");
        for (Task task : allTasks) {
            System.out.println("  ID: " + task.getId() +
                    ", Description: " + task.getDescription() +
                    ", Status: " + task.getStatus());
        }

        Task retrievedTask = taskDao.getTaskById(createdTask1.getId());
        System.out.println("Retrieved Task: " + retrievedTask.getDescription());

        retrievedTask.setDescription("Learn Java and JDBC");
        retrievedTask.setStatus(TaskStatus.MARK_IN_PROGRESS);
        taskDao.updateTask(retrievedTask);
        System.out.println("Task Updated");

        Task updatedTask = taskDao.getTaskById(createdTask1.getId());
        System.out.println("Updated Task: " + updatedTask.getDescription() + ", Status: "+ updatedTask.getStatus());

        taskDao.deleteTask(createdTask2.getId());
        System.out.println("Task 2 Deleted");

        allTasks = taskDao.getAllTasks();
        System.out.println("All Tasks after deletion:");
        for (Task task : allTasks) {
            System.out.println("  ID: " + task.getId() + ", Description: " + task.getDescription() + ", Status: " + task.getStatus());
        }

        dataSource.close();
    }
}
