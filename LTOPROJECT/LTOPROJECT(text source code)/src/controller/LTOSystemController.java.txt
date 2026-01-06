package controller;

import model.*;
import utils.InputValidator;
import java.util.LinkedList;
import java.util.List;

/**
 * Main controller class for the LTO System.
 * 
 * This demonstrates:
 * - Separation of concerns: Business logic separated from GUI and data access
 * - Encapsulation: Data is managed through this controller
 */
public class LTOSystemController {
    private List<User> users;
    private List<Violation> violations;
    private List<DriversLicense> licenses;

    public LTOSystemController() {
        this.users = new LinkedList<>();
        this.violations = new LinkedList<>();
        this.licenses = new LinkedList<>();
        initializeSystem();
    }

    private void initializeSystem() {
        loadUsers();
        loadViolations();
        loadLicenses();
        
        if (users.isEmpty()) {
            // Create default users
            try {
                User admin = new Admin("Admin User", "admin123", "admin@lto.gov");
                User officer = new Officer("Officer User", "officer123", "officer@lto.gov");
                users.add(admin);
                users.add(officer);
                saveUsers();
            } catch (Exception e) {
                System.out.println("Error creating default users: " + e.getMessage());
            }
        }
    }

    // Getters
    public List<User> getUsers() { return new LinkedList<>(users); }
    public List<Violation> getViolations() { return new LinkedList<>(violations); }
    public List<DriversLicense> getLicenses() { return new LinkedList<>(licenses); }

    // File operations
    public void loadUsers() {
        this.users = FileHandler.loadUsers();
    }

    public void loadViolations() {
        this.violations = FileHandler.loadViolations();
    }

    public void loadLicenses() {
        this.licenses = FileHandler.loadLicenses();
    }

    public void saveUsers() {
        try {
            FileHandler.saveUsers(users);
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public void saveViolations() {
        try {
            FileHandler.saveViolations(violations);
        } catch (Exception e) {
            System.out.println("Error saving violations: " + e.getMessage());
        }
    }

    public void saveLicenses() {
        try {
            FileHandler.saveLicenses(licenses);
        } catch (Exception e) {
            System.out.println("Error saving licenses: " + e.getMessage());
        }
    }

    public void saveAll() {
        saveUsers();
        saveViolations();
        saveLicenses();
    }

    // User operations
    public User login(String fullName, String password, Role role) {
        for (User user : users) {
            if (user.getFullName().equalsIgnoreCase(fullName) && 
                user.getPassword().equals(password) &&
                user.getRole() == role) {
                return user;
            }
        }
        return null;
    }

    public boolean registerDriver(String fullName, String password, String email, String licenseId) {
        if (!InputValidator.isValidName(fullName)) return false;
        if (!InputValidator.isValidPassword(password)) return false;
        if (!InputValidator.isValidEmail(email)) return false;
        if (!InputValidator.isValidLicenseId(licenseId)) return false;

        // Check for duplicate license ID
        for (User u : users) {
            if (u.getLicenseId() != null && u.getLicenseId().equalsIgnoreCase(licenseId)) {
                return false;
            }
        }

        try {
            DriversLicense license = new DriversLicense(licenseId, fullName, "N/A", new java.util.Date());
            User newUser = new Driver(fullName, password, email, licenseId);
            users.add(newUser);
            licenses.add(license);
            saveUsers();
            saveLicenses();
            return true;
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addOfficer(String fullName, String password, String email) {
        if (!InputValidator.isValidName(fullName)) return false;
        if (!InputValidator.isValidPassword(password)) return false;
        if (!InputValidator.isValidEmail(email)) return false;

        User newOfficer = new Officer(fullName, password, email);
        users.add(newOfficer);
        saveUsers();
        return true;
    }

    // Violation operations
    public Violation addViolation(String violatorName, String licenseNumber, ViolationType type) {
        int newId = violations.size() + 1;
        Violation violation = new Violation(newId, violatorName, licenseNumber, type, type.getBaseFine(), false);
        violations.add(violation);
        
        // Link to license if exists
        for (DriversLicense license : licenses) {
            if (license.getLicenseNumber().equals(licenseNumber)) {
                license.addViolation(violation);
                break;
            }
        }
        
        saveViolations();
        return violation;
    }

    public boolean removeViolation(int violationId) {
        Violation toRemove = null;
        for (Violation v : violations) {
            if (v.getId() == violationId) {
                toRemove = v;
                break;
            }
        }
        if (toRemove != null) {
            violations.remove(toRemove);
            saveViolations();
            return true;
        }
        return false;
    }

    public List<Violation> getViolationsByUser(String userName) {
        List<Violation> userViolations = new LinkedList<>();
        for (Violation v : violations) {
            if (v.getViolatorName().equalsIgnoreCase(userName)) {
                userViolations.add(v);
            }
        }
        return userViolations;
    }

    public DriversLicense findLicense(String licenseNumber) {
        for (DriversLicense license : licenses) {
            if (license.getLicenseNumber().equalsIgnoreCase(licenseNumber)) {
                return license;
            }
        }
        return null;
    }

    public User findUserByPlate(String plateNumber) {
        for (User user : users) {
            if (user.getPlateNumbers().contains(plateNumber.toUpperCase())) {
                return user;
            }
        }
        return null;
    }
}

