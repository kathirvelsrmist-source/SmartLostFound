import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DatabaseConnection {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/lost_found_db?useSSL=false&serverTimezone=UTC";

    private static final String DB_USER = "root";

    // Replace this with your actual MySQL root password.
    private static final String DB_PASSWORD = "#Kathir17";

    // Fixed application administrator credentials.
    private static final String ADMIN_USERNAME = "kathir";
    private static final String ADMIN_PASSWORD = "#KathirSB17";
    private static final String ADMIN_NAME = "Kathir (Administrator)";
    private static final String ADMIN_PHONE = "N/A";
    private static final String ADMIN_ROLE = "ADMIN";

    private DatabaseConnection() {
        // Utility class - no objects needed.
    }

    public static Connection getConnection() throws SQLException {

        try {
            // Explicitly load MySQL Connector/J driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL Connector/J driver was not found. "
                    + "Make sure the Connector/J JAR is included in the runtime classpath.",
                    e
            );
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {

        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(20), " +
                "role VARCHAR(10) NOT NULL DEFAULT 'USER'" +
                ")";

        String createItemsTable =
                "CREATE TABLE IF NOT EXISTS items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "type VARCHAR(10) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "category VARCHAR(50), " +
                "description VARCHAR(500), " +
                "colour VARCHAR(30), " +
                "location VARCHAR(100), " +
                "item_date DATE, " +
                "reporter_username VARCHAR(50) NOT NULL, " +
                "contact VARCHAR(50), " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "image_path VARCHAR(255), " +
                "CONSTRAINT fk_reporter_username " +
                "FOREIGN KEY (reporter_username) " +
                "REFERENCES users(username) ON DELETE CASCADE" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createItemsTable);

            createDefaultAdminIfMissing(conn);

        } catch (SQLException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to MySQL.\n\n" +
                    "Please check:\n" +
                    "1. MySQL Server is running\n" +
                    "2. The 'lost_found_db' database exists\n" +
                    "3. DB_PASSWORD in DatabaseConnection.java " +
                    "matches your real MySQL password\n\n" +
                    "Error details: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void createDefaultAdminIfMissing(Connection conn)
            throws SQLException {

        String checkSql =
                "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement checkStmt =
                     conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, ADMIN_USERNAME);

            try (ResultSet rs = checkStmt.executeQuery()) {

                if (rs.next()) {
                    return;
                }
            }
        }

        String insertSql =
                "INSERT INTO users " +
                "(username, password, name, phone, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement insertStmt =
                     conn.prepareStatement(insertSql)) {

            insertStmt.setString(1, ADMIN_USERNAME);
            insertStmt.setString(2, ADMIN_PASSWORD);
            insertStmt.setString(3, ADMIN_NAME);
            insertStmt.setString(4, ADMIN_PHONE);
            insertStmt.setString(5, ADMIN_ROLE);

            insertStmt.executeUpdate();
        }
    }
}