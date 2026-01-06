package model;

/**
 * Officer user class extending User.
 * 
 * This demonstrates:
 * - Inheritance (Module 2): Officer extends User
 * - Polymorphism (Module 2): Overrides displayMenu() and getMenuOptions()
 */
public class Officer extends User {
    
    public Officer(String fullName, String password, String email) {
        super(fullName, password, email, Role.OFFICER, null);
    }

    /**
     * Overridden method demonstrating polymorphism.
     */
    @Override
    public String[] getMenuOptions() {
        return new String[]{
            "Add Violation by License",
            "Add Violation by Plate Number",
            "Search Violations",
            "Delete Violation",
            "Logout"
        };
    }

    /**
     * Overridden method demonstrating runtime polymorphism.
     */
    @Override
    public void displayMenu() {
        System.out.println("\n=== Officer Menu ===");
        String[] options = getMenuOptions();
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
}

