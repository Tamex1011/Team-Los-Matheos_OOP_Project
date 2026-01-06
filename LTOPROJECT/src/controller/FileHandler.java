package controller;

import model.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * FileHandler class for managing file I/O operations.
 * 
 * This demonstrates:
 * - Separation of concerns: File operations separated from business logic
 * - Exception handling (Module 5): Proper try-catch blocks
 * - Encapsulation: File operations are encapsulated in this class
 */
public class FileHandler {
    private static final String DATA_FILE = "data/violations.csv";
    private static final String USER_FILE = "data/users.csv";
    private static final String LICENSE_FILE = "data/licenses.csv";

    /**
     * Load violations from file.
     * Demonstrates exception handling (Module 5).
     */
    public static List<Violation> loadViolations() {
        List<Violation> violations = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    violations.add(Violation.fromCSV(line));
                } catch (Exception e) {
                    System.out.println("Warning: Invalid violation data skipped: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No violations file found - starting with empty list");
        }
        return violations;
    }

    /**
     * Save violations to file.
     * Demonstrates exception handling (Module 5).
     */
    public static void saveViolations(List<Violation> violations) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Violation v : violations) {
                writer.write(v.toCSV() + "\n");
            }
        }
    }

    /**
     * Load users from file.
     */
    public static List<User> loadUsers() {
        List<User> users = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    users.add(User.fromCSV(line));
                } catch (Exception e) {
                    System.out.println("Warning: Invalid user data skipped: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No users file found - starting with empty list");
        }
        return users;
    }

    /**
     * Save users to file.
     */
    public static void saveUsers(List<User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {
                writer.write(u.toCSV() + "\n");
            }
        }
    }

    /**
     * Load licenses from file.
     */
    public static List<DriversLicense> loadLicenses() {
        List<DriversLicense> licenses = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LICENSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    licenses.add(DriversLicense.fromCSV(line));
                } catch (Exception e) {
                    System.out.println("Warning: Invalid license data skipped: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No licenses file found - starting with empty list");
        }
        return licenses;
    }

    /**
     * Save licenses to file.
     */
    public static void saveLicenses(List<DriversLicense> licenses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LICENSE_FILE))) {
            for (DriversLicense license : licenses) {
                writer.write(license.toCSV() + "\n");
            }
        }
    }
}

