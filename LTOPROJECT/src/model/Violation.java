package model;

import java.util.Date;

/**
 * Violation class representing a traffic violation.
 * 
 * This demonstrates:
 * - Encapsulation (Module 2): Private fields with getters/setters
 * - Interface implementation (Module 3): Implements Payable and Recordable
 * 
 * Why implement Payable interface?
 * - Violations can be paid, but so could other things (fees, taxes, etc.)
 * - Interface allows us to treat all payable items uniformly
 * - Enables polymorphism - we can process payment for any Payable object
 */
public class Violation implements Payable, Recordable {
    private final int id;
    private final String violatorName;
    private final String licenseNumber;
    private final ViolationType type;
    private double fine;
    private boolean approved;
    private boolean paid;
    private Date dateIssued;
    private String additionalPenalty;

    public Violation(int id, String violatorName, String licenseNumber, ViolationType type, double fine, boolean approved) {
        this.id = id;
        this.violatorName = violatorName;
        this.licenseNumber = licenseNumber;
        this.type = type;
        this.fine = type.getBaseFine();
        this.approved = approved;
        this.paid = false;
        this.dateIssued = new Date();
        this.additionalPenalty = getDefaultPenalty(type);
    }

    private String getDefaultPenalty(ViolationType type) {
        return switch (type) {
            case NO_LICENSE, SUSPENDED_LICENSE -> "1-year disqualification from obtaining a driver's license";
            case RECKLESS_DRIVING_2ND -> "3-month license suspension";
            case RECKLESS_DRIVING_3RD -> "6-month license suspension";
            case DUI -> "Possible imprisonment or license revocation";
            case NO_SEATBELT_3RD -> "1-week license suspension";
            default -> "";
        };
    }

    // Getters (Encapsulation)
    public int getId() { 
        return id; 
    }
    
    public String getViolatorName() { 
        return violatorName; 
    }
    
    public String getLicenseNumber() { 
        return licenseNumber; 
    }
    
    public ViolationType getType() { 
        return type; 
    }
    
    public double getFine() { 
        return fine; 
    }
    
    public boolean isApproved() { 
        return approved; 
    }
    
    public Date getDateIssued() { 
        return new Date(dateIssued.getTime()); // Return copy for encapsulation
    }
    
    public String getAdditionalPenalty() { 
        return additionalPenalty; 
    }

    // Setters
    public void setApproved(boolean approved) { 
        this.approved = approved; 
    }
    
    public void setFine(double fine) { 
        this.fine = fine; 
    }
    
    public void setAdditionalPenalty(String additionalPenalty) { 
        this.additionalPenalty = additionalPenalty; 
    }

    // Interface implementation: Payable
    @Override
    public boolean isPaid() {
        return paid;
    }

    @Override
    public double getAmount() {
        return fine;
    }

    /**
     * Implements Payable interface - processes payment for this violation.
     * This demonstrates interface implementation (Module 3).
     */
    @Override
    public void processPayment() throws IllegalStateException {
        if (!this.approved) {
            throw new IllegalStateException("Cannot pay an unapproved violation");
        }
        if (this.paid) {
            throw new IllegalStateException("Violation has already been paid");
        }
        this.paid = true;
    }
    
    public void setPaid(boolean paid) { 
        this.paid = paid; 
    }

    // Interface implementation: Recordable
    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%.2f,%b,%b,%d,%s", 
            id, violatorName, licenseNumber, type.name(), fine, approved, paid, dateIssued.getTime(), additionalPenalty);
    }

    public static Violation fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid violation data format");
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        if (parts[0].isEmpty()) {
            throw new IllegalArgumentException("Empty violation data");
        }
        
        try {
            ViolationType type = ViolationType.valueOf(parts[3]);
            Violation v = new Violation(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                type,
                type.getBaseFine(),
                Boolean.parseBoolean(parts[5])
            );
            v.paid = Boolean.parseBoolean(parts[6]);
            v.dateIssued = new Date(Long.parseLong(parts[7]));
            if (parts.length > 8) {
                v.additionalPenalty = parts[8];
            } else {
                v.additionalPenalty = "";
            }
            return v;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid violation type: " + parts[3]);
        }
    }

    @Override
    public String toString() {
        String status = paid ? "PAID" : (approved ? "APPROVED" : "PENDING");
        String result = String.format("ID: %d | Name: %s | Type: %s | Fine: ₱%,.2f | Status: %s | Date: %s",
                id, violatorName, type.getDescription(), fine, status, dateIssued.toString());
        if (!additionalPenalty.isEmpty()) {
            result += String.format(" | Additional Penalty: %s", additionalPenalty);
        }
        return result;
    }
}

