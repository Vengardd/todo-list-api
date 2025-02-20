import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// consider using dependency injection for the connection details
@SuppressWarnings("unused")
public class TaskDao {

    private static final Logger LOGGER = Logger.getLogger(TaskDao.class.getName());
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public TaskDao(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public Task createTask(Task task) throws TaskDaoException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        String sql = "INSERT INTO tasks (description, status, created_at, updated_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, task.getDescription());
            stmt.setString(2, task.getStatus());
            stmt.setObject(3, task.getCreatedAt());
            stmt.setObject(4, task.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new TaskDaoException("Task creation failed. No rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                } else {
                    throw new TaskDaoException("Task creation failed. No ID obtained.");
                }
            }

            return task;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating task", e);
            throw new TaskDaoException("Error creating task", e);
        }
    }

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public Task getTaskById(int id) throws TaskDaoException {
        String sql = "SELECT id, description, status, created_at, updated_at FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task(rs.getString("description"), rs.getString("status"));
                    task.setId(rs.getInt("id"));
                    return task;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting task by ID: " + id, e);
            throw new TaskDaoException("Error getting task by ID: " + id, e);
        }
    }

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public List<Task> getAllTasks() throws TaskDaoException {
        String sql = "SELECT id FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task task = new Task(rs.getString("description"), rs.getString("status"));
                task.setId(rs.getInt("id"));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all tasks", e);
            throw new TaskDaoException("Error getting all tasks", e);
        }
    }

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public void updateTask(Task task) throws TaskDaoException {
        String sql = "UPDATE tasks SET description = ?, status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new TaskDaoException("Task update failed. No rows affected. Task ID: " + task.getId());
            }
            LOGGER.log(Level.INFO, "Task updated successfully: {0}", task);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating task: " + task.getId(), e);
            throw new TaskDaoException("Error updating task: " + task.getId(), e);
        }
    }

    // consider using dependency injection for the connection details
    @SuppressWarnings("unused")
    public void deleteTask(int id) throws TaskDaoException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new TaskDaoException("Deleting task failed, no rows affected. Task ID: " + id);
            }
            LOGGER.log(Level.INFO, "Task deleted successfully: {0}", id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting task: " + id, e);
            throw new TaskDaoException("Error deleting task: " + id, e);
        }
    }

    class TaskDaoException extends Exception {
        public TaskDaoException(String message) {
            super(message);
        }

        public TaskDaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
