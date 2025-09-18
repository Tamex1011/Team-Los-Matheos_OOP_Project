/**
 * Parent class representing a User in the system
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String role;
    
    /**
     * Constructor for User class
     */
    public User(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Method to handle user login
     */
    public void login() {
        System.out.println("User " + name + " (ID: " + userId + ") has logged in.");
    }
    
    /**
     * Method to handle user logout
     */
    public void logout() {
        System.out.println("User " + name + " (ID: " + userId + ") has logged out.");
    }
    
    /**
     * Method to update user profile
     */
    public void updateProfile() {
        System.out.println("Profile updated for user " + name + " (ID: " + userId + ").");
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
