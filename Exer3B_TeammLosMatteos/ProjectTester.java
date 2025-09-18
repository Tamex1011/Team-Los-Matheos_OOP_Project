import java.util.Date;

/**
 * ProjectTester class to demonstrate the usage of all classes and subclasses
 */
public class ProjectTester {
    public static void main(String[] args) {
        System.out.println("=== Project Tester - Class Hierarchy Demonstration ===\n");
        
        // Create instances of each class
        System.out.println("1. Creating User instance:");
        User user = new User(1001, "John Doe", "john.doe@email.com", "General User");
        System.out.println(user);
        user.login();
        user.updateProfile();
        user.logout();
        System.out.println();
        
        // Create LTO instance
        System.out.println("2. Creating LTO instance:");
        Date ltoIssueDate = new Date();
        Date ltoExpiryDate = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 year from now
        LTO lto = new LTO(2001, "Maria Santos", "maria.santos@lto.gov.ph", "LTO Officer", 3001, ltoIssueDate, ltoExpiryDate);
        System.out.println(lto);
        lto.login();
        lto.issue();
        lto.renew();
        lto.logout();
        System.out.println();
        
        // Create License instance
        System.out.println("3. Creating License instance:");
        Date licenseDueDate = new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // 30 days from now
        License license = new License(3001, "Pedro Cruz", "pedro.cruz@email.com", "License Holder", 4001, "Professional Driver's License", "Active", licenseDueDate);
        System.out.println(license);
        license.login();
        license.apply();
        license.updateStatus();
        license.logout();
        System.out.println();
        
        // Create Application instance
        System.out.println("4. Creating Application instance:");
        Date appSubmittedDate = new Date();
        Application application = new Application(4001, "Ana Reyes", "ana.reyes@email.com", "Applicant", 5001, "Under Review", appSubmittedDate);
        System.out.println(application);
        application.login();
        application.submit();
        application.withdraw();
        application.logout();
        System.out.println();
        
        // Demonstrate polymorphism
        System.out.println("5. Demonstrating Polymorphism:");
        User[] users = {user, lto, license, application};
        
        for (User u : users) {
            System.out.println("Processing: " + u.getClass().getSimpleName());
            u.login();
            u.updateProfile();
            u.logout();
            System.out.println("---");
        }
        
        System.out.println("\n=== All tests completed successfully! ===");
    }
}
