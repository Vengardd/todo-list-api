package com.dominik.tasktracker.commands;

import com.dominik.tasktracker.model.Task;
import com.dominik.tasktracker.TaskDAO;
import com.dominik.tasktracker.TaskOperation;
import com.dominik.tasktracker.model.TaskStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class SetDone implements TaskOperation {
    private final TaskDAO taskDAO;
    private final int taskId;

    public SetDone(TaskDAO taskDAO, int taskId) {
        this.taskDAO = taskDAO;
        this.taskId = taskId;
    }

    @Override
    public void execute() throws Exception {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setStatus(TaskStatus.DONE);
        taskDAO.updateTask(task);
    }
}
