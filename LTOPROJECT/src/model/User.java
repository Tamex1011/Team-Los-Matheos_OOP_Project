package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all users in the LTO system.
 * 
 * This demonstrates:
 * - Abstract classes (Module 3): User is abstract because we never create
 *   a generic "User" - we always create specific types (Admin, Officer, Driver)
 * - Inheritance (Module 2): Admin, Officer, and Driver extend this class
 * - Encapsulation (Module 2): Private fields with getters/setters
 * - Polymorphism (Module 2): displayMenu() method is overridden in subclasses
 */
public abstract class User {
    private final String fullName;
    private final String password;
    private final String email;
    private final Role role;
    private String licenseId;
    private final List<String> plateNumbers;

    protected User(String fullName, String password, String email, Role role, String licenseId) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.licenseId = licenseId;
        this.plateNumbers = new ArrayList<>();
    }

    // Getters (Encapsulation - Module 2)
    public String getFullName() { 
        return fullName; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public Role getRole() { 
        return role; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getLicenseId() { 
        return licenseId; 
    }
    
    public List<String> getPlateNumbers() { 
        return new ArrayList<>(plateNumbers); // Return copy for encapsulation
    }
    
    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
    
    public void addPlateNumber(String plateNumber) { 
        this.plateNumbers.add(plateNumber); 
    }
    
    public void removePlateNumber(String plateNumber) { 
        this.plateNumbers.remove(plateNumber); 
    }

    /**
     * Abstract method that must be implemented by subclasses.
     * This demonstrates polymorphism - each user type has different menu options.
     * 
     * @return menu options as a string array
     */
    public abstract String[] getMenuOptions();
    
    /**
     * Abstract method for displaying role-specific menu.
     * This demonstrates method overriding and runtime polymorphism.
     */
    public abstract void displayMenu();

    /**
     * Convert user to CSV format for file storage.
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s", 
            fullName, password, email, role, 
            licenseId != null ? licenseId : "", 
            String.join(";", plateNumbers));
    }

    /**
     * Create User from CSV line.
     * Uses factory pattern to create appropriate subclass.
     */
    public static User fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid user data format");
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        
        Role role = Role.valueOf(parts[3].toUpperCase());
        String licenseId = parts.length > 4 ? parts[4] : null;
        
        // Factory pattern - create appropriate subclass based on role
        User user = switch (role) {
            case ADMIN -> new Admin(parts[0], parts[1], parts[2]);
            case OFFICER -> new Officer(parts[0], parts[1], parts[2]);
            case DRIVER -> new Driver(parts[0], parts[1], parts[2], licenseId);
        };
        
        if (parts.length > 5 && !parts[5].isEmpty()) {
            String[] plates = parts[5].split(";");
            for (String plate : plates) {
                if (!plate.isEmpty()) {
                    user.addPlateNumber(plate.trim());
                }
            }
        }
        return user;
    }
}

