package com.dominik.tasktracker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TaskDaoTest {
    private HikariDataSource dataSource;
    private TaskDao taskDao;

    @BeforeEach
    public void setUp() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/cli_task_tracker");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setMaximumPoolSize(5);

        dataSource = new HikariDataSource(config);
        taskDao = new TaskDao(dataSource);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS tasks");
            stmt.execute("CREATE TABLE tasks (" +
                    "id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "description VARCHAR(255) NOT NULL," +
                    "status VARCHAR(20) NOT NULL," +
                    "created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()," +
                    "updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()" +
                    ")");

            stmt.execute("CREATE OR REPLACE FUNCTION update_updated_at_column()" +
                    "RETURNS TRIGGER AS $$" +
                    "BEGIN" +
                    "    NEW.updated_at = now();" +
                    "    RETURN NEW;" +
                    "END;" +
                    "$$ language 'plpgsql'");

            stmt.execute("CREATE TRIGGER update_updated_at_column " +
                    "BEFORE UPDATE ON tasks " +
                    "FOR EACH ROW " +
                    "EXECUTE PROCEDURE update_updated_at_column()");
        }
    }

    @AfterEach
    public void tearDown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Test
    public void testCreateTask() throws TaskDao.TaskDaoException {
        Task task = new Task("Test task", "MARK_IN_PROGRESS");
        Task createdTask = taskDao.createTask(task);

        assertNotNull(createdTask.getId(), "Task ID should not be null after creation.");
        assertEquals("Test task", createdTask.getDescription(), "Task description should match.");
        assertEquals(TaskStatus.MARK_IN_PROGRESS, createdTask.getStatus(), "Task status should match.");
    }

    @Test
    public void testGetTaskById() throws TaskDao.TaskDaoException {
        Task task = new Task("Find task by ID", "MARK_IN_PROGRESS");
        Task createdTask = taskDao.createTask(task);

        Task retrievedTask = taskDao.getTaskById(createdTask.getId());

        assertNotNull(retrievedTask, "Retrieved task should not be null.");
        assertEquals(createdTask.getId(), retrievedTask.getId(), "Task IDs should match.");
        assertEquals("Find task by ID", retrievedTask.getDescription(), "Task descriptions should match.");
    }

    @Test
    public void testUpdateTask() throws TaskDao.TaskDaoException {
        Task task = new Task("Original description", "MARK_IN_PROGRESS");
        Task createdTask = taskDao.createTask(task);

        createdTask.setDescription("Updated description");
        createdTask.setStatus(TaskStatus.MARK_DONE);
        taskDao.updateTask(createdTask);

        Task updatedTask = taskDao.getTaskById(createdTask.getId());
        assertEquals("Updated description", updatedTask.getDescription(), "Description should be updated.");
        assertEquals(TaskStatus.MARK_DONE, updatedTask.getStatus(), "Status should be updated.");
    }

    @Test
    public void testDeleteTask() throws TaskDao.TaskDaoException {
        Task task = new Task("Task to delete", "MARK_IN_PROGRESS");
        Task createdTask = taskDao.createTask(task);

        taskDao.deleteTask(createdTask.getId());

        Task deletedTask = taskDao.getTaskById(createdTask.getId());
        assertNull(deletedTask, "Task should be null after deletion.");
    }

    @Test
    public void testGetAllTasks() throws TaskDao.TaskDaoException {
        taskDao.createTask(new Task("Task 1", "MARK_IN_PROGRESS"));
        taskDao.createTask(new Task("Task 2", "MARK_DONE"));
        taskDao.createTask(new Task("Task 3", "MARK_IN_PROGRESS"));

        List<Task> tasks = taskDao.getAllTasks();

        assertEquals(3, tasks.size(), "Should retrieve all 3 tasks.");
    }

    @Test
    public void testCreateTaskWithEmptyDescription() {
        Task task = new Task("", "MARK_IN_PROGRESS");

        assertThrows(IllegalArgumentException.class, () -> taskDao.createTask(task),
                "Should throw IllegalArgumentException for empty description.");
    }

    @Test
    public void testGetNonExistentTask() throws TaskDao.TaskDaoException {
        Task task = taskDao.getTaskById(9999);

        assertNull(task, "Should return null for non-existent task.");
    }
}
