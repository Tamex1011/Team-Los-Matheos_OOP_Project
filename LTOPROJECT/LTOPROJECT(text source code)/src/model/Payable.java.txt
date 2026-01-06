package model;

/**
 * Interface for objects that can be paid.
 * This demonstrates the use of interfaces in OOP (Module 3).
 * 
 * Why use an interface here?
 * - Multiple unrelated classes can implement payment behavior
 * - Provides a contract that classes must follow
 * - Enables polymorphism - we can treat all Payable objects the same way
 */
public interface Payable {
    /**
     * Process payment for this payable item.
     * @throws IllegalStateException if payment cannot be processed
     */
    void processPayment() throws IllegalStateException;
    
    /**
     * Check if this item has been paid.
     * @return true if paid, false otherwise
     */
    boolean isPaid();
    
    /**
     * Get the amount to be paid.
     * @return the fine/amount
     */
    double getAmount();
}

