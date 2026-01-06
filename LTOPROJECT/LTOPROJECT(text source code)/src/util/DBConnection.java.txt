package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple JDBC connection helper.
 * Adjust URL/USER/PASSWORD to match your local MySQL setup.
 * Requires mysql-connector-j-9.4.0.jar on the classpath.
 */
public final class DBConnection {
    // TODO: update credentials to your environment
    private static final String URL = "jdbc:mysql://localhost:3306/lto_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Rever123";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        // DriverManager will load com.mysql.cj.jdbc.Driver from the classpath
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

