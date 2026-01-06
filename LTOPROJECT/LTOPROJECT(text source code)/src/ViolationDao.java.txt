import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViolationDao {

    private static final String SELECT_ALL = """
        SELECT id, violator_name, license_number, violation_type, fine, approved, paid, date_issued, additional_penalty
        FROM violations
    """;

    private static final String DELETE_ALL = "DELETE FROM violations";

    private static final String INSERT_SQL = """
        INSERT INTO violations (id, violator_name, license_number, violation_type, fine, approved, paid, date_issued, additional_penalty)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    public List<LTOSystem.Violation> findAll() throws SQLException {
        List<LTOSystem.Violation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LTOSystem.ViolationType type;
                try {
                    type = LTOSystem.ViolationType.valueOf(rs.getString("violation_type"));
                } catch (IllegalArgumentException e) {
                    continue;
                }
                LTOSystem.Violation v = new LTOSystem.Violation(
                        rs.getInt("id"),
                        rs.getString("violator_name"),
                        rs.getString("license_number"),
                        type,
                        rs.getDouble("fine"),
                        rs.getBoolean("approved")
                );
                v.setPaid(rs.getBoolean("paid"));
                v.setDateIssued(new Date(rs.getTimestamp("date_issued").getTime()));
                String penalty = rs.getString("additional_penalty");
                if (penalty != null) {
                    v.setAdditionalPenalty(penalty);
                }
                list.add(v);
            }
        }
        return list;
    }

    public void replaceAll(List<LTOSystem.Violation> violations) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(DELETE_ALL);
            }
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
                for (LTOSystem.Violation v : violations) {
                    ps.setInt(1, v.getId());
                    ps.setString(2, v.getViolatorName());
                    ps.setString(3, v.getLicenseNumber());
                    ps.setString(4, v.getType().name());
                    ps.setDouble(5, v.getFine());
                    ps.setBoolean(6, v.isApproved());
                    ps.setBoolean(7, v.isPaid());
                    ps.setTimestamp(8, new java.sql.Timestamp(v.getDateIssued().getTime()));
                    ps.setString(9, v.getAdditionalPenalty());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    // Individual CRUD operations

    /**
     * Create a new violation in the database.
     * @param violation The violation to create
     * @return true if creation was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean create(LTOSystem.Violation violation) throws SQLException {
        String insertSql = """
            INSERT INTO violations (id, violator_name, license_number, violation_type, fine, approved, paid, date_issued, additional_penalty)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, violation.getId());
            ps.setString(2, violation.getViolatorName());
            ps.setString(3, violation.getLicenseNumber());
            ps.setString(4, violation.getType().name());
            ps.setDouble(5, violation.getFine());
            ps.setBoolean(6, violation.isApproved());
            ps.setBoolean(7, violation.isPaid());
            ps.setTimestamp(8, new java.sql.Timestamp(violation.getDateIssued().getTime()));
            ps.setString(9, violation.getAdditionalPenalty());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Find a violation by ID.
     * @param id The violation ID to search for
     * @return The violation if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public LTOSystem.Violation findById(int id) throws SQLException {
        String selectSql = """
            SELECT id, violator_name, license_number, violation_type, fine, approved, paid, date_issued, additional_penalty
            FROM violations
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LTOSystem.ViolationType type;
                    try {
                        type = LTOSystem.ViolationType.valueOf(rs.getString("violation_type"));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                    LTOSystem.Violation v = new LTOSystem.Violation(
                            rs.getInt("id"),
                            rs.getString("violator_name"),
                            rs.getString("license_number"),
                            type,
                            rs.getDouble("fine"),
                            rs.getBoolean("approved")
                    );
                    v.setPaid(rs.getBoolean("paid"));
                    v.setDateIssued(new Date(rs.getTimestamp("date_issued").getTime()));
                    String penalty = rs.getString("additional_penalty");
                    if (penalty != null) {
                        v.setAdditionalPenalty(penalty);
                    }
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * Update an existing violation in the database.
     * @param violation The violation with updated data (must have valid ID)
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean update(LTOSystem.Violation violation) throws SQLException {
        String updateSql = """
            UPDATE violations
            SET violator_name = ?, license_number = ?, violation_type = ?, fine = ?, 
                approved = ?, paid = ?, date_issued = ?, additional_penalty = ?
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, violation.getViolatorName());
            ps.setString(2, violation.getLicenseNumber());
            ps.setString(3, violation.getType().name());
            ps.setDouble(4, violation.getFine());
            ps.setBoolean(5, violation.isApproved());
            ps.setBoolean(6, violation.isPaid());
            ps.setTimestamp(7, new java.sql.Timestamp(violation.getDateIssued().getTime()));
            ps.setString(8, violation.getAdditionalPenalty());
            ps.setInt(9, violation.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete a violation from the database by ID.
     * @param id The ID of the violation to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean delete(int id) throws SQLException {
        String deleteSql = "DELETE FROM violations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

