import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDao {

    private static final Logger LOGGER = Logger.getLogger(TaskDao.class.getName());
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    // consider using dependency injection for the connection details
    public TaskDao(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

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

    class TaskDaoException extends Exception {
        public TaskDaoException(String message) {
            super(message);
        }

        public TaskDaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
