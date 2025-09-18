import java.util.Date;

/**
 * Application class that inherits from User
 */
public class Application extends User {
    private int applicationId;
    private String status;
    private Date submittedDate;
    
    /**
     * Constructor for Application class
     */
    public Application(int userId, String name, String email, String role, int applicationId, String status, Date submittedDate) {
        super(userId, name, email, role);
        this.applicationId = applicationId;
        this.status = status;
        this.submittedDate = submittedDate;
    }
    
    // Getters and Setters
    public int getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getSubmittedDate() {
        return submittedDate;
    }
    
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    /**
     * Method to submit an application
     */
    public void submit() {
        System.out.println("Application " + applicationId + " submitted by " + getName() + " on " + submittedDate);
    }
    
    /**
     * Method to withdraw an application
     */
    public void withdraw() {
        System.out.println("Application " + applicationId + " withdrawn by " + getName());
    }
    
    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", status='" + status + '\'' +
                ", submittedDate=" + submittedDate +
                "} " + super.toString();
    }
}
