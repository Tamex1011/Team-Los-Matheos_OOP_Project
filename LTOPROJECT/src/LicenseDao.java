import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LicenseDao {

    private static final String SELECT_ALL = """
        SELECT license_number, full_name, address, birth_date
        FROM licenses
    """;

    private static final String DELETE_ALL = "DELETE FROM licenses";

    private static final String INSERT_SQL = """
        INSERT INTO licenses (license_number, full_name, address, birth_date)
        VALUES (?, ?, ?, ?)
    """;

    public List<LTOSystem.DriversLicense> findAll() throws SQLException {
        List<LTOSystem.DriversLicense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LTOSystem.DriversLicense lic = new LTOSystem.DriversLicense(
                        rs.getString("license_number"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        new Date(rs.getTimestamp("birth_date").getTime())
                );
                list.add(lic);
            }
        }
        return list;
    }

    public void replaceAll(List<LTOSystem.DriversLicense> licenses) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(DELETE_ALL);
            }
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
                for (LTOSystem.DriversLicense lic : licenses) {
                    ps.setString(1, lic.getLicenseNumber());
                    ps.setString(2, lic.getFullName());
                    ps.setString(3, lic.getAddress());
                    ps.setTimestamp(4, new java.sql.Timestamp(lic.getBirthDate().getTime()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    // Individual CRUD operations

    /**
     * Create a new license in the database.
     * @param license The license to create
     * @return true if creation was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean create(LTOSystem.DriversLicense license) throws SQLException {
        String insertSql = """
            INSERT INTO licenses (license_number, full_name, address, birth_date)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, license.getLicenseNumber());
            ps.setString(2, license.getFullName());
            ps.setString(3, license.getAddress());
            ps.setTimestamp(4, new java.sql.Timestamp(license.getBirthDate().getTime()));
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Find a license by license number.
     * @param licenseNumber The license number to search for
     * @return The license if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public LTOSystem.DriversLicense findByLicenseNumber(String licenseNumber) throws SQLException {
        String selectSql = """
            SELECT license_number, full_name, address, birth_date
            FROM licenses
            WHERE license_number = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, licenseNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LTOSystem.DriversLicense(
                            rs.getString("license_number"),
                            rs.getString("full_name"),
                            rs.getString("address"),
                            new Date(rs.getTimestamp("birth_date").getTime())
                    );
                }
            }
        }
        return null;
    }

    /**
     * Update an existing license in the database.
     * @param licenseNumber The license number of the license to update
     * @param license The updated license data
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean update(String licenseNumber, LTOSystem.DriversLicense license) throws SQLException {
        String updateSql = """
            UPDATE licenses
            SET full_name = ?, address = ?, birth_date = ?
            WHERE license_number = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, license.getFullName());
            ps.setString(2, license.getAddress());
            ps.setTimestamp(3, new java.sql.Timestamp(license.getBirthDate().getTime()));
            ps.setString(4, licenseNumber);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete a license from the database by license number.
     * @param licenseNumber The license number of the license to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean delete(String licenseNumber) throws SQLException {
        String deleteSql = "DELETE FROM licenses WHERE license_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setString(1, licenseNumber);
            return ps.executeUpdate() > 0;
        }
    }
}

