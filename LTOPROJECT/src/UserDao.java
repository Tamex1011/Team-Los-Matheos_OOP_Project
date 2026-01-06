import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the users table.
 */
public class UserDao {

    private static final String SELECT_ALL = """
        SELECT id, full_name, password_plain, email, role, license_number, plate_numbers
        FROM users
    """;

    private static final String DELETE_ALL = "DELETE FROM users";

    private static final String INSERT_SQL = """
        INSERT INTO users (id, full_name, password_plain, email, role, license_number, plate_numbers)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    public List<LTOSystem.User> findAll() throws SQLException {
        List<LTOSystem.User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LTOSystem.User u = new LTOSystem.User(
                        rs.getString("full_name"),
                        rs.getString("password_plain"),
                        rs.getString("email"),
                        LTOSystem.Role.valueOf(rs.getString("role")),
                        rs.getString("license_number") == null || rs.getString("license_number").isEmpty()
                                ? null
                                : rs.getString("license_number")
                );
                String plates = rs.getString("plate_numbers");
                if (plates != null && !plates.isEmpty()) {
                    for (String p : plates.split(";")) {
                        if (!p.isEmpty()) {
                            u.addPlateNumber(p.trim());
                        }
                    }
                }
                list.add(u);
            }
        }
        return list;
    }

    public void replaceAll(List<LTOSystem.User> users) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(DELETE_ALL);
            }
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
                for (LTOSystem.User u : users) {
                    ps.setObject(1, null); // auto-increment
                    ps.setString(2, u.getFullName());
                    ps.setString(3, u.getPassword());
                    ps.setString(4, u.getEmail());
                    ps.setString(5, u.getRole().name());
                    ps.setString(6, u.getLicenseId());
                    ps.setString(7, String.join(";", u.getPlateNumbers()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    // Individual CRUD operations

    /**
     * Create a new user in the database.
     * @param user The user to create
     * @return The generated user ID, or -1 if creation failed
     * @throws SQLException if database error occurs
     */
    public int create(LTOSystem.User user) throws SQLException {
        String insertSql = """
            INSERT INTO users (full_name, password_plain, email, role, license_number, plate_numbers)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getLicenseId());
            ps.setString(6, String.join(";", user.getPlateNumbers()));
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Find a user by ID.
     * @param id The user ID to search for
     * @return The user if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public LTOSystem.User findById(int id) throws SQLException {
        String selectSql = """
            SELECT id, full_name, password_plain, email, role, license_number, plate_numbers
            FROM users
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LTOSystem.User u = new LTOSystem.User(
                            rs.getString("full_name"),
                            rs.getString("password_plain"),
                            rs.getString("email"),
                            LTOSystem.Role.valueOf(rs.getString("role")),
                            rs.getString("license_number") == null || rs.getString("license_number").isEmpty()
                                    ? null
                                    : rs.getString("license_number")
                    );
                    String plates = rs.getString("plate_numbers");
                    if (plates != null && !plates.isEmpty()) {
                        for (String p : plates.split(";")) {
                            if (!p.isEmpty()) {
                                u.addPlateNumber(p.trim());
                            }
                        }
                    }
                    return u;
                }
            }
        }
        return null;
    }

    /**
     * Update an existing user in the database.
     * @param id The ID of the user to update
     * @param user The updated user data
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean update(int id, LTOSystem.User user) throws SQLException {
        String updateSql = """
            UPDATE users
            SET full_name = ?, password_plain = ?, email = ?, role = ?, license_number = ?, plate_numbers = ?
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getLicenseId());
            ps.setString(6, String.join(";", user.getPlateNumbers()));
            ps.setInt(7, id);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete a user from the database by ID.
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean delete(int id) throws SQLException {
        String deleteSql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

