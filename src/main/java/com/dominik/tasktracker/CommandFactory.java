package com.dominik.tasktracker;

import com.dominik.tasktracker.commands.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandFactory {
    private final TaskDAO taskDAO;

    public CommandFactory(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public TaskOperation createCommand(String[] args) {
        String commandStr = args[0].toUpperCase();

        try {
            TaskCommand command = TaskCommand.valueOf(commandStr);
            return handleStandardCommand(command, args);
        } catch (IllegalArgumentException e) {
            if (commandStr.equals("IN_PROGRESS")) {
                int taskId = Integer.parseInt(args[1]);
                return new SetInProgress(taskDAO, taskId);
            } else if (commandStr.equals("DONE")) {
                int taskId = Integer.parseInt(args[1]);
                return new SetDone(taskDAO, taskId);
            }

            throw new IllegalArgumentException("Unknown command: " + args[0]);
        }
    }

    private TaskOperation handleStandardCommand(TaskCommand command, String[] args) {
        return switch (command) {
            case ADD -> {
                if (args.length < 2) {
                    throw new IllegalArgumentException("Usage: add <description>");
                }
                yield new AddTask(taskDAO, args[1]);
            }
            case UPDATE -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Usage: update <task_id> <new_description>");
                }
                yield new UpdateTask(taskDAO, Integer.parseInt(args[1]), args[2]);
            }
            case DELETE -> {
                if (args.length < 2) {
                    throw new IllegalArgumentException("Usage: delete <task_id>");
                }
                yield new DeleteTask(taskDAO, Integer.parseInt(args[1]));
            }
            case LIST -> new ListAllTasks(taskDAO);
            default -> throw new IllegalArgumentException(formatUnknownCommandError(command));

        };
    }

    private String formatUnknownCommandError(TaskCommand command) {
        return String.format("Unknown command: %s. Available commands: %s",
                command,
                Arrays.stream(TaskCommand.values())
                        .map(Enum::name)
                        .map(String::toLowerCase)
                        .collect(Collectors.joining(", ")
                        )
        );
    }
}
