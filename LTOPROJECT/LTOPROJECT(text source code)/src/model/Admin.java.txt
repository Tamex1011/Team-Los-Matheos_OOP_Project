package model;

/**
 * Admin user class extending User.
 * 
 * This demonstrates:
 * - Inheritance (Module 2): Admin extends User
 * - Polymorphism (Module 2): Overrides displayMenu() and getMenuOptions()
 * - Encapsulation (Module 2): Inherits private fields from User
 */
public class Admin extends User {
    
    public Admin(String fullName, String password, String email) {
        super(fullName, password, email, Role.ADMIN, null);
    }

    /**
     * Overridden method demonstrating polymorphism.
     * Each user role has different menu options.
     */
    @Override
    public String[] getMenuOptions() {
        return new String[]{
            "View All Users",
            "Search Users",
            "Remove Driver Violation",
            "Add New Officer",
            "Logout"
        };
    }

    /**
     * Overridden method demonstrating runtime polymorphism.
     * When we call displayMenu() on a User reference, the correct
     * implementation is called based on the actual object type.
     */
    @Override
    public void displayMenu() {
        System.out.println("\n=== Admin Menu ===");
        String[] options = getMenuOptions();
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
}

