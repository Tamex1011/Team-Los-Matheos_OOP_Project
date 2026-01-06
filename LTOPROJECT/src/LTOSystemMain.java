import javax.swing.SwingUtilities;

/**
 * Main entry point for the LTO Tracking System.
 * 
 * This demonstrates:
 * - Separation of concerns: Main class only initializes and launches the system
 * - Package organization (Module 4): Uses classes from different packages
 */
public class LTOSystemMain {
    public static void main(String[] args) {
        System.out.println("LTO Tracking System - Starting...");
        
        // Launch GUI on Event Dispatch Thread (required for Swing)
        SwingUtilities.invokeLater(() -> {
            System.out.println("Launching GUI...");
            new LTOSystem.LTOSystemGUI();
        });
    }
}

