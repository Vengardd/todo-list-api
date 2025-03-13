package com.dominik.tasktracker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static HikariDataSource dataSource;

    public static void main(String[] args) {
        try {
            initializeDataSource();
            if (args.length >= 2) {
                String command = args[0];
                TaskDAO taskDAO = new TaskDAO(dataSource);

                try {
                    executeCommand(args, taskDAO);
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

    private static void executeCommand(@NotNull String[] args,
                                       @NotNull TaskDAO taskDAO) throws Exception {
        CommandFactory factory = new CommandFactory(taskDAO);
        TaskOperation operation = factory.createCommand(args);
        operation.execute();
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