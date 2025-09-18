import java.util.Date;

/**
 * License class that inherits from User
 */
public class License extends User {
    private int licenseId;
    private String type;
    private String status;
    private Date dueDate;
    
    /**
     * Constructor for License class
     */
    public License(int userId, String name, String email, String role, int licenseId, String type, String status, Date dueDate) {
        super(userId, name, email, role);
        this.licenseId = licenseId;
        this.type = type;
        this.status = status;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public int getLicenseId() {
        return licenseId;
    }
    
    public void setLicenseId(int licenseId) {
        this.licenseId = licenseId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    /**
     * Method to apply for a license
     */
    public void apply() {
        System.out.println("License application submitted for " + getName() + " (License ID: " + licenseId + ", Type: " + type + ")");
    }
    
    /**
     * Method to update license status
     */
    public void updateStatus() {
        System.out.println("License status updated to '" + status + "' for " + getName() + " (License ID: " + licenseId + ")");
    }
    
    @Override
    public String toString() {
        return "License{" +
                "licenseId=" + licenseId +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", dueDate=" + dueDate +
                "} " + super.toString();
    }
}
