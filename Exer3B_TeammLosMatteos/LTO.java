import java.util.Date;

/**
 * LTO (Land Transportation Office) class that inherits from User
 */
public class LTO extends User {
    private int ltoId;
    private Date issueDate;
    private Date expiryDate;
    
    /**
     * Constructor for LTO class
     */
    public LTO(int userId, String name, String email, String role, int ltoId, Date issueDate, Date expiryDate) {
        super(userId, name, email, role);
        this.ltoId = ltoId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }
    
    // Getters and Setters
    public int getLtoId() {
        return ltoId;
    }
    
    public void setLtoId(int ltoId) {
        this.ltoId = ltoId;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    /**
     * Method to issue LTO documents
     */
    public void issue() {
        System.out.println("LTO " + ltoId + " issued on " + issueDate + " for " + getName());
    }
    
    /**
     * Method to renew LTO documents
     */
    public void renew() {
        System.out.println("LTO " + ltoId + " renewed. New expiry date: " + expiryDate + " for " + getName());
    }
    
    @Override
    public String toString() {
        return "LTO{" +
                "ltoId=" + ltoId +
                ", issueDate=" + issueDate +
                ", expiryDate=" + expiryDate +
                "} " + super.toString();
    }
}
