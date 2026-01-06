package model;

/**
 * Interface for objects that can be saved to and loaded from CSV files.
 * This demonstrates the use of interfaces in OOP (Module 3).
 * 
 * Why use an interface here?
 * - Defines a common contract for data persistence
 * - Allows different classes to implement their own CSV format
 * - Enables generic file handling code
 */
public interface Recordable {
    /**
     * Convert this object to CSV format.
     * @return CSV string representation
     */
    String toCSV();
    
    /**
     * Create an object from CSV string.
     * @param line CSV line to parse
     * @return the created object
     * @throws IllegalArgumentException if CSV format is invalid
     */
    static Recordable fromCSV(String line) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Must be implemented by subclasses");
    }
}

