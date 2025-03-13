package com.dominik.tasktracker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TaskDAOTest {
    private HikariDataSource dataSource;
    private TaskDAO taskDAO;
    private final String DONE = "DONE";
    private final String IN_PROGRESS = "IN_PROGRESS";

    @BeforeEach
    public void setUp() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/cli_task_tracker");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setMaximumPoolSize(5);

        dataSource = new HikariDataSource(config);
        taskDAO = new TaskDAO(dataSource);

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
    public void testTransactionRollback() throws SQLException, TaskDAO.TaskDAOException {
        Connection conn = dataSource.getConnection();
        String originalDescription = "Task before transaction";
        Task task = new Task(originalDescription, IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);
        int taskId = createdTask.getId();

        try {
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE tasks SET description = ? WHERE id = ?"
            );
            stmt.setString(1, "Updated in transaction");
            stmt.setInt(2, taskId);
            stmt.executeUpdate();

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT description FROM tasks WHERE id = ?"
            );
            checkStmt.setInt(1, taskId);
            ResultSet rs = checkStmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Updated in transaction", rs.getString("description"));

            conn.rollback();

            Task verifyTask = taskDAO.getTaskById(taskId);
            assertEquals(originalDescription, verifyTask.getDescription(),
                    "Description should be unchanged after rollback");
        } finally {
            conn.close();
        }
    }

    @Test
    public void testCreateTask() throws TaskDAO.TaskDAOException {
        Task task = new Task("Test task", IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);

        assertNotNull(createdTask.getId(), "Task ID should not be null after creation.");
        assertEquals("Test task", createdTask.getDescription(), "Task description should match.");
        assertEquals(TaskStatus.IN_PROGRESS, createdTask.getStatus(), "Task status should match.");
    }

    @Test
    public void testGetTaskById() throws TaskDAO.TaskDAOException {
        Task task = new Task("Find task by ID", IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);

        Task retrievedTask = taskDAO.getTaskById(createdTask.getId());

        assertNotNull(retrievedTask, "Retrieved task should not be null.");
        assertEquals(createdTask.getId(), retrievedTask.getId(), "Task IDs should match.");
        assertEquals("Find task by ID", retrievedTask.getDescription(), "Task descriptions should match.");
    }

    @Test
    public void testUpdateTask() throws TaskDAO.TaskDAOException {
        Task task = new Task("Original description", IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);

        createdTask.setDescription("Updated description");
        createdTask.setStatus(TaskStatus.DONE);
        taskDAO.updateTask(createdTask);

        Task updatedTask = taskDAO.getTaskById(createdTask.getId());
        assertEquals("Updated description", updatedTask.getDescription(), "Description should be updated.");
        assertEquals(TaskStatus.DONE, updatedTask.getStatus(), "Status should be updated.");
    }

    @Test
    public void testDeleteTask() throws TaskDAO.TaskDAOException {
        Task task = new Task("Task to delete", IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);

        taskDAO.deleteTask(createdTask.getId());

        Task deletedTask = taskDAO.getTaskById(createdTask.getId());
        assertNull(deletedTask, "Task should be null after deletion.");
    }

    @Test
    public void testGetAllTasks() throws TaskDAO.TaskDAOException {
        taskDAO.createTask(new Task("Task 1", IN_PROGRESS));
        taskDAO.createTask(new Task("Task 2", DONE));
        taskDAO.createTask(new Task("Task 3", IN_PROGRESS));

        List<Task> tasks = taskDAO.getAllTasks();

        assertEquals(3, tasks.size(), "Should retrieve all 3 tasks.");
    }

    @Test
    public void testCreateTaskWithEmptyDescription() {
        Task task = new Task("", IN_PROGRESS);

        assertThrows(IllegalArgumentException.class, () -> taskDAO.createTask(task),
                "Should throw IllegalArgumentException for empty description.");
    }

    @Test
    public void testGetNonExistentTask() throws TaskDAO.TaskDAOException {
        Task task = taskDAO.getTaskById(9999);

        assertNull(task, "Should return null for non-existent task.");
    }

    @Test
    public void testMaxDescriptionLength() {
        String maxLengthDescription = "X".repeat(255);
        Task validTask = new Task(maxLengthDescription, IN_PROGRESS);

        assertDoesNotThrow(() -> taskDAO.createTask(validTask));

        String tooLongDescription = "X".repeat(256);
        Task invalidTask = new Task(tooLongDescription, IN_PROGRESS);

        assertThrows(SQLException.class, () -> {
            try {
                taskDAO.createTask(invalidTask);
            } catch (TaskDAO.TaskDAOException e) {
                if (e.getCause() instanceof SQLException) {
                    throw (SQLException) e.getCause();
                }
                throw e;
            }
        });
    }

    @Test
    public void testInvalidTaskStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("Test task", "INVALID_STATUS");
        });
    }

    @Test
    public void testTaskWorkFlow() throws TaskDAO.TaskDAOException {
        Task task = new Task("New workflow task", IN_PROGRESS);
        Task createdTask = taskDAO.createTask(task);
        int taskId = createdTask.getId();

        Task initialTask = taskDAO.getTaskById(taskId);
        assertEquals(TaskStatus.IN_PROGRESS, initialTask.getStatus());

        initialTask.setDescription("Updated workflow task");
        taskDAO.updateTask(initialTask);

        Task updatedTask = taskDAO.getTaskById(taskId);
        updatedTask.setStatus(TaskStatus.DONE);
        taskDAO.updateTask(updatedTask);

        Task finalTask = taskDAO.getTaskById(taskId);
        assertEquals("Updated workflow task", finalTask.getDescription());
        assertEquals(TaskStatus.DONE, finalTask.getStatus());

        taskDAO.deleteTask(taskId);
        assertNull(taskDAO.getTaskById(taskId));
    }
}
