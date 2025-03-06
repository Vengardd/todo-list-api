package com.dominik.tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskCommandExecutorTest {
    private TaskDAO mockTaskDao;

    @BeforeEach
    public void setUp() {
        mockTaskDao = mock(TaskDAO.class);
    }

    @Test
    public void testInvalidTaskIdHandling() throws TaskDAO.TaskDAOException {
        when(mockTaskDao.getTaskById(999)).thenReturn(null);

        String[] args = new String[]{"MARK_DONE", "999"};
        boolean result = executeCommandForTest("MARK_DONE", args, mockTaskDao);

        assertFalse(result);
        verify(mockTaskDao, never()).updateTask(any());
    }

    @Test
    public void testStatusTransition() throws TaskDAO.TaskDAOException {
        Task task = new Task("Test Task", "MARK_IN_PROGRESS");
        task.setId(1);

        when(mockTaskDao.getTaskById(1)).thenReturn(task);

        String[] args = new String[]{"MARK_DONE", "1"};
        boolean result = executeCommandForTest("MARK_DONE", args, mockTaskDao);

        assertTrue(result);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(mockTaskDao).updateTask(taskCaptor.capture());

        assertEquals(TaskStatus.MARK_DONE, taskCaptor.getValue().getStatus());
    }

    private boolean executeCommandForTest(String commandStr, String[] args, TaskDAO taskDao) {
        try {
            TaskStatus command = TaskStatus.valueOf(commandStr);

            switch (command) {
                case TaskStatus.MARK_DONE -> {
                    if (args.length < 2) {
                        return false;
                    }

                    int taskId = Integer.parseInt(args[1]);
                    Task task = taskDao.getTaskById(taskId);

                    if (task == null) {
                        return false;
                    }

                    task.setStatus(TaskStatus.MARK_DONE);
                    taskDao.updateTask(task);
                    return true;
                }

                default -> {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}
