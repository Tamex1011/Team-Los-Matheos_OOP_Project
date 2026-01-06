import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.swing.*;

public class LTOSystem {
    // Enums
    public static enum Role {
        DRIVER, OFFICER, ADMIN
    }

    public static enum ViolationType {
        // License-related violations
        NO_VALID_LICENSE(3000, "Driving without a valid driver's license"),
        EXPIRED_LICENSE(3000, "Driving with an expired driver's license"),
        SUSPENDED_REVOKED_LICENSE(3000, "Driving with a suspended or revoked license"),
        FAKE_TAMPERED_LICENSE(3000, "Driving with fake or tampered driver's license"),
        IMPROPER_LICENSE_CLASS(3000, "Driving with improper or incorrect license classification"),
        STUDENT_PERMIT_NO_COMPANION(3000, "Student permit holder driving without licensed companion"),
        FAILURE_CARRY_LICENSE(1000, "Failure to carry driver's license while driving"),
        FAKE_DOCUMENTS_LICENSE(3000, "Submission of fake documents for license application"),
        NO_LICENSE(3000, "Driving without a valid license"),
        SUSPENDED_LICENSE(3000, "Driving with a suspended/revoked or improper license"),
        NO_LICENSE_OR_CR(1000, "Failure to carry driver's license, OR/CR while driving"),
        NO_PLATE(2000, "Driving without a valid plate number"),
        DUI(50000, "Driving under the influence of alcohol/drugs"),
        
        // Reckless and careless driving
        RECKLESS_DRIVING_1ST(2000, "Reckless driving (1st offense)"),
        RECKLESS_DRIVING_2ND(3000, "Reckless driving (2nd offense)"),
        RECKLESS_DRIVING_3RD(10000, "Reckless driving (3rd & subsequent)"),
        CARELESS_NEGLIGENT(1000, "Careless or negligent driving"),
        
        // DUI violations
        DUI_NO_INJURY(50000, "Driving under the influence of alcohol (no injury)"),
        DUI_WITH_INJURY(150000, "Driving under the influence causing physical injury"),
        DUI_HOMICIDE(500000, "Driving under the influence causing homicide"),
        
        // Traffic signs and signals
        DISREGARD_TRAFFIC_SIGNS(1000, "Disregarding traffic signs"),
        DISREGARD_TRAFFIC_LIGHTS(1000, "Disregarding traffic lights or signals"),
        FAILURE_STOP_LINES(1000, "Failure to stop at designated stop lines or intersections"),
        
        // Overtaking and passing
        OVERTAKING_PROHIBITED(1000, "Overtaking in prohibited areas"),
        PASSING_WRONG_SIDE(1000, "Passing on the wrong side of the road"),
        
        // Traffic obstruction
        OBSTRUCTION_TRAFFIC(1000, "Obstruction of traffic"),
        
        // Parking violations
        ILLEGAL_PARKING(1000, "Illegal parking"),
        PARKING_PEDESTRIAN_LANES(1000, "Parking on pedestrian lanes, intersections, or driveways"),
        
        // Right of way
        FAILURE_GIVE_WAY_PEDESTRIANS(1000, "Failure to give way to pedestrians"),
        FAILURE_YIELD_RIGHT_OF_WAY(1000, "Failure to yield right of way"),
        
        // Turning and signaling
        UNSAFE_BACKING_TURNING(1000, "Unsafe backing or turning"),
        FAILURE_SIGNAL_TURNING(1000, "Failure to signal before turning or stopping"),
        
        // Seatbelt violations
        NO_SEATBELT_1ST(1000, "Failure to wear seatbelt (1st offense)"),
        NO_SEATBELT_2ND(2000, "Failure to wear seatbelt (2nd offense)"),
        NO_SEATBELT_3RD(5000, "Failure to wear seatbelt (3rd offense)"),
        FAILURE_REQUIRE_SEATBELT(3000, "Failure to require passengers to wear seatbelt"),
        
        // Helmet violations
        NO_HELMET_1ST(1500, "Failure to wear standard motorcycle helmet (1st offense)"),
        NO_HELMET_2ND(3000, "Failure to wear standard motorcycle helmet (2nd offense)"),
        NO_HELMET_3RD(5000, "Failure to wear standard motorcycle helmet (3rd offense)"),
        NO_HELMET_4TH(10000, "Failure to wear standard motorcycle helmet (4th offense)"),
        BACK_RIDER_NO_HELMET(1500, "Allowing back rider without helmet"),
        MOTORCYCLE_OVERLOAD_PASSENGERS(1000, "Motorcycle carrying more than allowed passengers"),
        
        // Vehicle registration violations
        UNREGISTERED_VEHICLE(10000, "Driving an unregistered motor vehicle"),
        EXPIRED_REGISTRATION(10000, "Driving with expired vehicle registration"),
        FAILURE_CARRY_CR(1000, "Failure to carry Certificate of Registration (CR)"),
        FAILURE_CARRY_OR(1000, "Failure to carry Official Receipt (OR)"),
        TAMPERING_REGISTRATION(5000, "Tampering vehicle registration documents"),
        
        // Vehicle modifications
        UNAUTHORIZED_MODIFICATION(5000, "Unauthorized modification of motor vehicle"),
        UNAUTHORIZED_COLOR_CHANGE(5000, "Unauthorized change of vehicle color"),
        
        // Vehicle defects
        DEFECTIVE_LIGHTS(5000, "Operating a vehicle with defective lights"),
        DEFECTIVE_BRAKES(5000, "Operating a vehicle with defective brakes"),
        DEFECTIVE_SIGNAL_DEVICES(5000, "Operating a vehicle with defective signal devices"),
        NO_REQUIRED_ACCESSORIES(5000, "Operating a vehicle without required accessories"),
        
        // Special vehicle violations
        RIGHT_HAND_DRIVE(50000, "Operating a right-hand drive motor vehicle"),
        FAKE_IMPROVISED_PLATES(5000, "Use of fake or improvised license plates"),
        IMPROPER_DISPLAY_PLATES(5000, "Improper display of license plates"),
        
        // Overloading
        OVERLOADING_PASSENGERS(1000, "Overloading of passengers"),
        OVERLOADING_CARGO(1000, "Overloading of cargo"),
        LOAD_EXTENDING_WITHOUT_PERMIT(1000, "Load extending beyond allowed projection without permit"),
        AXLE_OVERLOAD(5000, "Axle overload / exceeding weight limits"),
        
        // Towing
        UNSAFE_TOWING(1000, "Unsafe towing of another vehicle"),
        
        // Mobile phone violations
        MOBILE_PHONE_1ST(5000, "Use of mobile phone while driving (1st offense)"),
        MOBILE_PHONE_2ND(10000, "Use of mobile phone while driving (2nd offense)"),
        MOBILE_PHONE_3RD(15000, "Use of mobile phone while driving (3rd offense)"),
        
        // Smoke belching
        SMOKE_BELCHING_1ST(2000, "Smoke belching (1st offense)"),
        SMOKE_BELCHING_2ND(4000, "Smoke belching (2nd offense)"),
        SMOKE_BELCHING_3RD(6000, "Smoke belching (3rd offense)"),
        
        // PUV violations
        PUV_NO_FRANCHISE(200000, "Operating a public utility vehicle without franchise (colorum)"),
        REFUSAL_CONVEY_PASSENGERS(5000, "Refusal to convey passengers"),
        OVERCHARGING_UNDERCHARGING(5000, "Overcharging or undercharging fare"),
        FAILURE_DISPLAY_FARE_MATRIX(5000, "Failure to display fare matrix"),
        FAILURE_GRANT_PWD_SENIOR_DISCOUNT(5000, "Failure to grant PWD/Senior discounts"),
        UNAUTHORIZED_DRIVER_PUV(5000, "Allowing unauthorized driver to operate PUV"),
        
        // Other violations
        FAILURE_COMPLY_TRAFFIC_ENFORCERS(2000, "Failure to comply with lawful orders of traffic enforcers"),
        VEHICLE_USED_IN_CRIME(10000, "Driving a motor vehicle used in the commission of a crime"),
        OTHER_TRAFFIC_VIOLATIONS(1000, "Other violations of traffic rules under RA 4136");

        private final double baseFine;
        private final String description;

        ViolationType(double baseFine, String description) {
            this.baseFine = baseFine;
            this.description = description;
        }

        public double getBaseFine() {
            return baseFine;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description + " (₱" + String.format("%,.2f", baseFine) + ")";
        }
    }

    // Classes
    public static class User {
        private String fullName;
        private String password;
        private Role role;
        private String email;
        private String licenseId;
        private List<String> plateNumbers;

        public User(String fullName, String password, String email, Role role, String licenseId) {
            this.fullName = fullName;
            this.password = password;
            this.email = email;
            this.role = role;
            this.licenseId = licenseId;
            this.plateNumbers = new ArrayList<>();
        }
 
        public String getFullName() { return fullName; }
        public String getPassword() { return password; }
        public Role getRole() { return role; }
        public String getEmail() { return email; }
        public String getLicenseId() { return licenseId; }
        public List<String> getPlateNumbers() { return plateNumbers; }
        public void addPlateNumber(String plateNumber) { this.plateNumbers.add(plateNumber); }
        public void removePlateNumber(String plateNumber) { this.plateNumbers.remove(plateNumber); }

        public String toCSV() {
            return String.format("%s,%s,%s,%s,%s,%s", 
                fullName, password, email, role, 
                licenseId != null ? licenseId : "", 
                String.join(";", plateNumbers));
        }

        public static User fromCSV(String line) {
            String[] parts = line.split(",");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Invalid user data format");
            }
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            User user = new User(parts[0], parts[1], parts[2], Role.valueOf(parts[3].toUpperCase()), 
                               parts.length > 4 ? parts[4] : null);
            if (parts.length > 5 && !parts[5].isEmpty()) {
                String[] plates = parts[5].split(";");
                for (String plate : plates) {
                    if (!plate.isEmpty()) {
                        user.addPlateNumber(plate.trim());
                    }
                }
            }
            return user;
        }
    }

    public static class DriversLicense {
        private String licenseNumber;
        private String fullName;
        private String address;
        private Date birthDate;
        private List<Violation> violations;
        private double totalUnpaidFines;

        public DriversLicense(String licenseNumber, String fullName, String address, Date birthDate) {
            this.licenseNumber = licenseNumber;
            this.fullName = fullName;
            this.address = address;
            this.birthDate = birthDate;
            this.violations = new ArrayList<>();
            this.totalUnpaidFines = 0;
        }

        public void addViolation(Violation violation) {
            violations.add(violation);
            if (!violation.isPaid()) {
                totalUnpaidFines += violation.getFine();
            }
        }

        public String toCSV() {
            return String.format("%s,%s,%s,%d",
                licenseNumber, fullName, address, birthDate.getTime());
        }

        public static DriversLicense fromCSV(String line) {
            String[] parts = line.split(",");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Invalid license data format");
            }
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            return new DriversLicense(
                parts[0],
                parts[1],
                parts[2],
                new Date(Long.parseLong(parts[3]))
            );
        }

        public String getLicenseNumber() { return licenseNumber; }
        public String getFullName() { return fullName; }
        public String getAddress() { return address; }
        public Date getBirthDate() { return birthDate; }
        public List<Violation> getViolations() { return violations; }
        public double getTotalUnpaidFines() { return totalUnpaidFines; }
    }

    public static class Violation {
        private int id;
        private String violatorName;
        private String licenseNumber;
        private ViolationType type;
        private double fine;
        private boolean approved;
        private boolean rejected;
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
            this.rejected = false;
            this.paid = false;
            this.dateIssued = new Date();
            this.additionalPenalty = getDefaultPenalty(type);
        }

        private String getDefaultPenalty(ViolationType type) {
            switch (type) {
                case NO_LICENSE:
                case SUSPENDED_LICENSE:
                    return "1-year disqualification from obtaining a driver's license";
                case RECKLESS_DRIVING_2ND:
                    return "3-month license suspension";
                case RECKLESS_DRIVING_3RD:
                    return "6-month license suspension";
                case DUI:
                    return "Possible imprisonment or license revocation";
                case NO_SEATBELT_3RD:
                    return "1-week license suspension";
                default:
                    return "";
            }
        }

        @Override
        public String toString() {
            String status = getStatusString();
            String result = String.format("ID: %d | Name: %s | Type: %s | Fine: ₱%,.2f | Status: %s | Date: %s",
                    id, violatorName, type.getDescription(), fine, status, dateIssued.toString());
            if (!additionalPenalty.isEmpty()) {
                result += String.format(" | Additional Penalty: %s", additionalPenalty);
            }
            return result;
        }

        public String getStatusString() {
            if (paid) return "PAID";
            if (approved) return "APPROVED";
            if (rejected) return "REJECTED";
            return "PENDING";
        }

        public String toCSV() {
            return String.format("%d,%s,%s,%s,%.2f,%b,%b,%b,%d,%s", 
                id, violatorName, licenseNumber, type.name(), fine, approved, rejected, paid, dateIssued.getTime(), additionalPenalty);
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
                double fine = Double.parseDouble(parts[4]);
                Violation v = new Violation(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    parts[2],
                    type,
                    type.getBaseFine(),
                    Boolean.parseBoolean(parts[5])
                );
                // Handle backward compatibility: old format has 8 parts, new format has 9+ parts
                if (parts.length >= 9) {
                    // New format: approved, rejected, paid
                    v.rejected = Boolean.parseBoolean(parts[6]);
                    v.paid = Boolean.parseBoolean(parts[7]);
                    v.dateIssued = new Date(Long.parseLong(parts[8]));
                    if (parts.length > 9) {
                        v.additionalPenalty = parts[9];
                    }
                } else {
                    // Old format: approved, paid (no rejected field)
                    v.rejected = false;
                    v.paid = Boolean.parseBoolean(parts[6]);
                    v.dateIssued = new Date(Long.parseLong(parts[7]));
                    if (parts.length > 8) {
                        v.additionalPenalty = parts[8];
                    }
                }
                return v;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid violation type: " + parts[3]);
            }
        }

        public int getId() { return id; }
        public String getViolatorName() { return violatorName; }
        public String getLicenseNumber() { return licenseNumber; }
        public ViolationType getType() { return type; }
        public double getFine() { return fine; }
        public boolean isApproved() { return approved; }
        public boolean isRejected() { return rejected; }
        public boolean isPaid() { return paid; }
        public Date getDateIssued() { return dateIssued; }
        public String getAdditionalPenalty() { return additionalPenalty; }

        public void setApproved(boolean approved) { 
            this.approved = approved;
            if (approved) {
                this.rejected = false; // Can't be both approved and rejected
            }
        }
        public void setRejected(boolean rejected) { 
            this.rejected = rejected;
            if (rejected) {
                this.approved = false; // Can't be both approved and rejected
            }
        }
        public void setFine(double fine) { this.fine = fine; }
        public void setAdditionalPenalty(String additionalPenalty) { this.additionalPenalty = additionalPenalty; }
        public void setPaid(boolean paid) { this.paid = paid; }
        public void setDateIssued(Date dateIssued) { this.dateIssued = dateIssued; }
        
        public void processPayment() {
            if (!this.approved) {
                if (this.rejected) {
                    throw new IllegalStateException("Cannot pay a rejected violation");
                } else {
                    throw new IllegalStateException("Cannot pay a pending violation. Please wait for admin approval.");
                }
            }
            if (this.paid) {
                throw new IllegalStateException("Violation has already been paid");
            }
            this.paid = true;
            
            if (this.licenseNumber != null) {
                DriversLicense license = LTOSystem.licenses.stream()
                    .filter(l -> l.getLicenseNumber().equals(this.licenseNumber))
                    .findFirst()
                    .orElse(null);
                if (license != null) {
                    license.getViolations().stream()
                        .filter(v -> v.getId() == this.id)
                        .findFirst()
                        .ifPresent(v -> v.setPaid(true));
                    license.totalUnpaidFines -= this.fine;
                }
            }
        }
    }

    // Main class fields
    public static LinkedList<User> users = new LinkedList<>();
    public static LinkedList<Violation> violations = new LinkedList<>();
    public static LinkedList<DriversLicense> licenses = new LinkedList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "violations.csv";
    private static final String USER_FILE = "users.csv";
    private static final String LICENSE_FILE = "licenses.csv";

    // Regular expressions for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9@#$%^&+=]{6,20}$");
    private static final Pattern LICENSE_ID_PATTERN = Pattern.compile("^[A-Z]\\d{2}-\\d{2}-\\d{6}$");
    private static final Pattern AUTOMOBILE_PLATE_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{4}$");
    private static final Pattern MOTORCYCLE_PLATE_PATTERN = Pattern.compile("^[A-Z]{2}-\\d{5}$");

    private static final String BACK_OPTION = "b";

    private static String getInput(String prompt) {
        System.out.print(prompt + " (type 'b' to go back): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase(BACK_OPTION)) {
            return null;
        }
        return input;
    }

    private static Integer getIntInput(String prompt) {
        while (true) {
            String input = getInput(prompt);
            if (input == null) return null;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Double getDoubleInput(String prompt) {
        while (true) {
            String input = getInput(prompt);
            if (input == null) return null;
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Boolean getBooleanInput(String prompt) {
        while (true) {
            String input = getInput(prompt);
            if (input == null) return null;
            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true")) {
                return true;
            } else if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("false")) {
                return false;
            }
            System.out.println("Please enter 'yes' or 'no'.");
        }
    }

    private static String getValidLicenseId(String prompt) {
        while (true) {
            final String inputLicenseId = getInput(prompt);
            if (inputLicenseId == null) return null;
            final String licenseId = inputLicenseId.toUpperCase();
            
            if (!LICENSE_ID_PATTERN.matcher(licenseId).matches()) {
                System.out.println("Invalid license ID format. Please use format: A01-23-456789");
                continue;
            }
            
            boolean exists = users.stream()
                .filter(u -> u.getLicenseId() != null)
                .anyMatch(u -> u.getLicenseId().equals(licenseId));
            
            if (exists) {
                System.out.println("This license ID is already registered to another user.");
                continue;
            }
            
            return licenseId;
        }
    }

    private static String getValidPlateNumber(String prompt) {
        while (true) {
            System.out.println("\nPlate Number Format:");
            System.out.println("1. Automobile (e.g., ABC-1234)");
            System.out.println("2. Motorcycle (e.g., AB-12345)");
            System.out.print("Select vehicle type (1-2): ");
            
            String typeChoice = scanner.nextLine().trim();
            if (typeChoice.equals(BACK_OPTION)) return null;
            
            Pattern pattern;
            String format;
            if (typeChoice.equals("1")) {
                pattern = AUTOMOBILE_PLATE_PATTERN;
                format = "ABC-1234";
            } else if (typeChoice.equals("2")) {
                pattern = MOTORCYCLE_PLATE_PATTERN;
                format = "AB-12345";
            } else {
                System.out.println("Invalid choice. Please select 1 or 2.");
                continue;
            }

            String input = getInput("Enter plate number (format: " + format + ")");
            if (input == null) return null;
            
            String plateNumber = input.toUpperCase().trim();
            
            if (!pattern.matcher(plateNumber).matches()) {
                System.out.println("Invalid plate number format. Please use format: " + format);
                continue;
            }
            
            boolean exists = users.stream()
                .anyMatch(u -> u.getPlateNumbers().contains(plateNumber));
            
            if (exists) {
                System.out.println("This plate number is already registered to another user.");
                continue;
            }
            
            return plateNumber;
        }
    }

    public static void main(String[] args) {
        System.out.println("LTOSystem main() started");
        initializeSystem();
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("Launching GUI...");
            new LTOSystem.LTOSystemGUI();
        });
    }

    private static void initializeSystem() {
        loadUsers();
        loadViolations();
        loadLicenses();
        if (users.isEmpty()) {
            try {
                User admin = new User("Admin User", "admin123", "admin@lto.gov", Role.ADMIN, null);
                User officer = new User("Officer User", "officer123", "officer@lto.gov", Role.OFFICER, null);
                users.add(admin);
                users.add(officer);
                saveUsers();
            } catch (Exception e) {
                System.out.println("Error creating default users: " + e.getMessage());
            }
        }
    }

    private static void register() {
        System.out.println("\n=== Driver Registration ===");
        
        // Add name validation pattern (includes diacritics/accents and Ñ/ñ)
        // Using Unicode property class to include all letters including Ñ/ñ
        // Only one space between words allowed
        Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}Ññ]+(\\s[\\p{L}Ññ]+)*$", Pattern.UNICODE_CHARACTER_CLASS);
        
        String fullName;
        while (true) {
            fullName = getInput("Enter your full name (letters and spaces only)");
            if (fullName == null) {
                System.out.println("Registration cancelled.");
                return;
            }
            
            if (!NAME_PATTERN.matcher(fullName).matches()) {
                System.out.println("Invalid name format. Please use only letters and spaces.");
                continue;
            }
            break;
        }

        String password = getInput("Enter password (6-20 characters)");
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            System.out.println("Invalid password format.");
            return;
        }

        String email;
        boolean isValidEmail;
        do {
            email = getInput("Enter email (must contain at least one letter before @)");
            if (email == null) {
                System.out.println("Registration cancelled.");
                return;
            }
            
            isValidEmail = true;
            
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format. Please use a valid email address.");
                isValidEmail = false;
                continue;
            }
            
            // Check if there's at least one letter before @
            String[] parts = email.split("@");
            if (parts.length != 2 || !parts[0].matches(".*[a-zA-Z].*")) {
                System.out.println("Email must contain at least one letter before @ symbol.");
                isValidEmail = false;
                continue;
            }
        } while (!isValidEmail);

        String licenseId = getValidLicenseId("Enter license ID (A01-23-456789)");
        if (licenseId == null) return;

        try {
            DriversLicense license = new DriversLicense(licenseId, fullName, "N/A", new Date());
            User newUser = new User(fullName, password, email, Role.DRIVER, licenseId);
            
            try {
                // Save to licenses.csv
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(LICENSE_FILE, true))) {
                    writer.write(license.toCSV() + "\n");
                }

                // Save to users.csv
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
                    writer.write(newUser.toCSV() + "\n");
                }

                users.add(newUser);
                licenses.add(license);

                System.out.println("\nRegistration successful!");
                System.out.println("License Number: " + licenseId);
                System.out.println("Name: " + fullName);

            } catch (IOException e) {
                System.out.println("Error saving data: " + e.getMessage());
                return;
            }
            
            Boolean addPlates = getBooleanInput("Would you like to register vehicle plates? (yes/no)");
            if (addPlates != null && addPlates) {
                manageVehiclePlates(newUser);
                saveUsers();
            }
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static User login(Role expectedRole) {
        // Add name validation pattern (includes diacritics/accents and Ñ/ñ)
        // Using Unicode property class to include all letters including Ñ/ñ
        // Only one space between words allowed
        Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}Ññ]+(\\s[\\p{L}Ññ]+)*$", Pattern.UNICODE_CHARACTER_CLASS);
        
        String fullName;
        while (true) {
            fullName = getInput("Full Name");
            if (fullName == null) return null;
            
            if (expectedRole == Role.ADMIN && !NAME_PATTERN.matcher(fullName).matches()) {
                System.out.println("Invalid name format. Please use only letters and spaces.");
                continue;
            }
            break;
        }

        String password = getInput("Password");
        if (password == null) return null;

        for (User user : users) {
            if (user.getFullName().equalsIgnoreCase(fullName) && 
                user.getPassword().equals(password) &&
                user.getRole() == expectedRole) {
                return user;
            }
        }
        System.out.println("Invalid credentials or insufficient privileges.");
        return null;
    }

    private static void handleUserMenu(User user) {
        switch (user.getRole()) {
            case ADMIN:
                adminMenu();
                break;
            case OFFICER:
                officerMenu();
                break;
            case DRIVER:
                driverMenu(user);
                break;
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View All Users");
            System.out.println("2. Search Users");
            System.out.println("3. Remove Driver Violation");
            System.out.println("4. Add New Officer");
            System.out.println("5. Logout");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayAllUsers();
                        break;
                    case 2:
                        searchUsers();
                        break;
                    case 3:
                        removeDriverViolation();
                        break;
                    case 4:
                        addNewOfficer();
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void displayAllUsers() {
        System.out.println("\n=== All Users ===");
        
        // Sort users by role
        List<User> drivers = new ArrayList<>();
        List<User> officers = new ArrayList<>();
        List<User> admins = new ArrayList<>();
        
        for (User user : users) {
            switch (user.getRole()) {
                case DRIVER:
                    drivers.add(user);
                    break;
                case OFFICER:
                    officers.add(user);
                    break;
                case ADMIN:
                    admins.add(user);
                    break;
            }
        }
        
        // Display Drivers
        System.out.println("\n=== Drivers ===");
        if (drivers.isEmpty()) {
            System.out.println("No drivers registered.");
        } else {
            for (User driver : drivers) {
                System.out.println("\nDriver Information:");
                System.out.println("Name: " + driver.getFullName());
                System.out.println("Email: " + driver.getEmail());
                System.out.println("License ID: " + (driver.getLicenseId() != null ? driver.getLicenseId() : "Not registered"));
                System.out.println("Registered Plates: " + (driver.getPlateNumbers().isEmpty() ? "None" : String.join(", ", driver.getPlateNumbers())));
            }
        }
        
        // Display Officers
        System.out.println("\n=== Officers ===");
        if (officers.isEmpty()) {
            System.out.println("No officers registered.");
        } else {
            for (User officer : officers) {
                System.out.println("\nOfficer Information:");
                System.out.println("Name: " + officer.getFullName());
                System.out.println("Email: " + officer.getEmail());
            }
        }
        
        // Display Admins
        System.out.println("\n=== Administrators ===");
        if (admins.isEmpty()) {
            System.out.println("No administrators registered.");
        } else {
            for (User admin : admins) {
                System.out.println("\nAdministrator Information:");
                System.out.println("Name: " + admin.getFullName());
                System.out.println("Email: " + admin.getEmail());
            }
        }
        
        // Display total counts
        System.out.println("\n=== User Statistics ===");
        System.out.println("Total Drivers: " + drivers.size());
        System.out.println("Total Officers: " + officers.size());
        System.out.println("Total Administrators: " + admins.size());
        System.out.println("Total Users: " + users.size());
    }

    private static void officerMenu() {
        while (true) {
            System.out.println("\n=== Officer Menu ===");
            System.out.println("1. Add Violation by License");
            System.out.println("2. Add Violation by Plate Number");
            System.out.println("3. Search Violations");
            System.out.println("4. Delete Violation");
            System.out.println("5. Logout");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addViolationByLicense();
                        break;
                    case 2:
                        addViolationByPlate();
                        break;
                    case 3:
                        searchViolations();
                        break;
                    case 4:
                        deleteViolation();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void driverMenu(User user) {
        while (true) {
            System.out.println("\n=== Driver Menu ===");
            System.out.println("1. View My Violations");
            System.out.println("2. Manage Vehicle Plates");
            System.out.println("3. Logout");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayUserViolations(user.getFullName());
                        break;
                    case 2:
                        manageVehiclePlates(user);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void manageVehiclePlates(User user) {
        while (true) {
            System.out.println("\n=== Vehicle Plate Management ===");
            System.out.println("Current registered plates: " + String.join(", ", user.getPlateNumbers()));
            System.out.println("1. Add new plate number");
            System.out.println("2. Remove plate number");
            System.out.println("3. Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        String newPlate = getValidPlateNumber("Enter new plate number");
                        if (newPlate == null) return;
                        user.addPlateNumber(newPlate);
                        System.out.println("Plate number added successfully!");
                        break;
                    case 2:
                        String plateToRemove = getInput("Enter plate number to remove");
                        if (plateToRemove == null) return;
                        if (user.getPlateNumbers().remove(plateToRemove.toUpperCase())) {
                            System.out.println("Plate number removed successfully!");
                        } else {
                            System.out.println("Plate number not found.");
                        }
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void displayUserViolations(String fullName) {
        System.out.println("\n=== Your Violations ===");
        List<Violation> userViolations = new ArrayList<>();
        double totalBalance = 0.0;
        double pendingTotal = 0.0;
        double approvedTotal = 0.0;
        
        // Reload violations to ensure we have the latest data
        loadViolations();
        
        // First collect all violations for this user
        for (Violation v : violations) {
            if (v.getViolatorName().equalsIgnoreCase(fullName)) {
                userViolations.add(v);
                // Add to appropriate total based on status
                if (v.isPaid()) {
                    // Skip paid violations in total
                } else if (v.isApproved()) {
                    approvedTotal += v.getFine();
                } else {
                    pendingTotal += v.getFine();
                }
                totalBalance += v.getFine();
            }
        }
        
        if (userViolations.isEmpty()) {
            System.out.println("No violations found for user: " + fullName);
            return;
        }
        
        // Display all violations
        for (Violation v : userViolations) {
            System.out.println(v);
        }
        
        // Display totals
        System.out.println("\n=== Violation Totals ===");
        if (pendingTotal > 0) {
            System.out.println("Pending Violations: ₱" + String.format("%,.2f", pendingTotal));
        }
        if (approvedTotal > 0) {
            System.out.println("Approved Violations: ₱" + String.format("%,.2f", approvedTotal));
        }
        System.out.println("Total Balance: ₱" + String.format("%,.2f", totalBalance));
        
        if (totalBalance > 0) {
            System.out.println("\n!!! PAY THE VIOLATION IN THE ADMINS OFFICE !!!");
        }
    }

    private static void viewLicenseDetails(User user) {
        DriversLicense userLicense = null;
        for (DriversLicense license : licenses) {
            if (license.getFullName().equalsIgnoreCase(user.getFullName())) {
                userLicense = license;
                break;
            }
        }

        if (userLicense == null) {
            System.out.println("No license found for your account.");
            return;
        }

        System.out.println("\n=== License Details ===");
        System.out.println("License Number: " + userLicense.getLicenseNumber());
        System.out.println("Full Name: " + userLicense.getFullName());
        System.out.println("Address: " + userLicense.getAddress());
        System.out.println("Birth Date: " + userLicense.getBirthDate());
        System.out.println("Total Unpaid Fines: ₱" + String.format("%,.2f", userLicense.getTotalUnpaidFines()));
        
        if (!userLicense.getViolations().isEmpty()) {
            System.out.println("\nViolation History:");
            for (Violation v : userLicense.getViolations()) {
                System.out.println(v);
            }
        }
    }

    private static void addViolation() {
        try {
            // Reload violations to get the latest data
            loadViolations();
            
            String name = getInput("Enter violator name");
            if (name == null) return;

            System.out.println("\nViolation Types:");
            ViolationType[] types = ViolationType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i]);
            }
            
            Integer typeChoice = getIntInput("Select violation type (1-" + types.length + ")");
            if (typeChoice == null) return;
            
            if (typeChoice >= 1 && typeChoice <= types.length) {
                ViolationType type = types[typeChoice - 1];
                double fine = type.getBaseFine();
                
                // Generate a unique ID based on current violations count
                int newId = violations.size() + 1;
                
                Violation violation = new Violation(newId, name, null, type, fine, false);
                violations.add(violation);
                saveViolations(); // Save immediately after adding
                System.out.println("Violation added successfully!");
            } else {
                System.out.println("Invalid violation type selected.");
            }
        } catch (Exception e) {
            System.out.println("Error adding violation: " + e.getMessage());
        }
    }

    private static void addViolationByLicense() {
        try {
            // Reload licenses and users to get the latest data
            loadLicenses();
            loadUsers();
            
            String licenseNumber = getInput("Enter driver's license number");
            if (licenseNumber == null) return;
            licenseNumber = licenseNumber.toUpperCase().trim();

            // First check in users.csv
            User user = null;
            for (User u : users) {
                if (u.getLicenseId() != null && u.getLicenseId().equalsIgnoreCase(licenseNumber)) {
                    user = u;
                    break;
                }
            }

            if (user != null) {
                System.out.println("\nDriver Information from User Database:");
                System.out.println("Name: " + user.getFullName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Registered Plate Numbers: " + String.join(", ", user.getPlateNumbers()));
            }

            // Then check in licenses.csv
            DriversLicense license = findLicense(licenseNumber);
            if (license == null) {
                System.out.println("\nLicense not found in license database.");
                if (user == null) {
                    System.out.println("No matching driver found in the system.");
                    System.out.println("\nAvailable licenses in system:");
                    for (DriversLicense l : licenses) {
                        System.out.println("License: " + l.getLicenseNumber() + " - Name: " + l.getFullName());
                    }
                    return;
                }
            } else {
                System.out.println("\nLicense Information from License Database:");
                System.out.println("Name: " + license.getFullName());
                System.out.println("License Expiry: " + license.getBirthDate());
                System.out.println("Total Unpaid Fines: ₱" + String.format("%,.2f", license.getTotalUnpaidFines()));
            }

            // Reload violations to get the latest data
            loadViolations();
            
            System.out.println("\nViolation Types:");
            ViolationType[] types = ViolationType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i]);
            }
            
            Integer typeChoice = getIntInput("Select violation type (1-" + types.length + ")");
            if (typeChoice == null) return;
            
            if (typeChoice >= 1 && typeChoice <= types.length) {
                ViolationType type = types[typeChoice - 1];
                double fine = type.getBaseFine();
                
                // Generate a unique ID based on current violations count
                int newId = violations.size() + 1;
                
                // Use the name from either user or license database
                String violatorName = user != null ? user.getFullName() : (license != null ? license.getFullName() : "Unknown");
                
                Violation violation = new Violation(newId, violatorName, licenseNumber, type, fine, false);
                violations.add(violation);
                
                if (license != null) {
                    license.addViolation(violation);
                }
                
                // Save violations immediately
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
                    writer.write(violation.toCSV() + "\n");
                    writer.flush();
                }
                
                System.out.println("Violation added successfully!");
            } else {
                System.out.println("Invalid violation type selected.");
            }
        } catch (Exception e) {
            System.out.println("Error adding violation: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
        }
    }

    private static void addViolationByPlate() {
        // Reload users to get the latest data
        loadUsers();
        
        System.out.println("\nPlate Number Format:");
        System.out.println("1. Automobile (e.g., ABC-1234)");
        System.out.println("2. Motorcycle (e.g., AB-12345)");
        System.out.print("Select vehicle type (1-2): ");
        
        String typeChoice = scanner.nextLine().trim();
        if (typeChoice.equals(BACK_OPTION)) return;
        
        Pattern pattern;
        String format;
        if (typeChoice.equals("1")) {
            pattern = AUTOMOBILE_PLATE_PATTERN;
            format = "ABC-1234";
        } else if (typeChoice.equals("2")) {
            pattern = MOTORCYCLE_PLATE_PATTERN;
            format = "AB-12345";
        } else {
            System.out.println("Invalid choice. Please select 1 or 2.");
            return;
        }

        String plateNumber = getInput("Enter plate number (format: " + format + ")");
        if (plateNumber == null) return;
        plateNumber = plateNumber.toUpperCase().trim();
        
        if (!pattern.matcher(plateNumber).matches()) {
            System.out.println("Invalid plate number format. Please use format: " + format);
            return;
        }

        // Check if plate number is registered to any user
        User violator = null;
        for (User user : users) {
            if (user.getPlateNumbers().contains(plateNumber)) {
                violator = user;
                break;
            }
        }

        String violatorName;
        String licenseId = null;

        if (violator != null) {
            System.out.println("\nDriver Information:");
            System.out.println("Name: " + violator.getFullName());
            System.out.println("License ID: " + violator.getLicenseId());
            violatorName = violator.getFullName();
            licenseId = violator.getLicenseId();
        } else {
            System.out.println("\nNo registered user found for this plate number.");
            violatorName = getInput("Enter violator's name");
            if (violatorName == null) return;
        }

        try {
            // Reload violations to get the latest data
            loadViolations();
            
            System.out.println("\nViolation Types:");
            ViolationType[] types = ViolationType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i]);
            }
            
            Integer typeChoice2 = getIntInput("Select violation type (1-" + types.length + ")");
            if (typeChoice2 == null) return;
            
            if (typeChoice2 >= 1 && typeChoice2 <= types.length) {
                ViolationType type = types[typeChoice2 - 1];
                double fine = type.getBaseFine();
                
                // Generate a unique ID based on current violations count
                int newId = violations.size() + 1;
                
                Violation violation = new Violation(newId, violatorName, licenseId, type, fine, false);
                violations.add(violation);
                
                if (licenseId != null) {
                    // Reload licenses to get the latest data
                    loadLicenses();
                    for (DriversLicense license : licenses) {
                        if (license.getLicenseNumber().equals(licenseId)) {
                            license.addViolation(violation);
                            break;
                        }
                    }
                }
                
                saveViolations(); // Save immediately after adding
                System.out.println("Violation added successfully!");
            } else {
                System.out.println("Invalid violation type selected.");
            }
        } catch (Exception e) {
            System.out.println("Error adding violation: " + e.getMessage());
        }
    }

    private static void loadViolations() {
        violations.clear();
        try {
            ViolationDao dao = new ViolationDao();
            violations.addAll(dao.findAll());
        } catch (SQLException e) {
            System.out.println("Error loading violations from DB: " + e.getMessage());
        }
    }

    public static void saveViolations() throws IOException {
        try {
            ViolationDao dao = new ViolationDao();
            dao.replaceAll(violations);
        } catch (SQLException e) {
            throw new IOException("Error saving violations to DB: " + e.getMessage(), e);
        }
    }

    private static void loadUsers() {
        users.clear();
        try {
            UserDao dao = new UserDao();
            users.addAll(dao.findAll());
        } catch (SQLException e) {
            System.out.println("Error loading users from DB: " + e.getMessage());
        }
    }

    private static void loadLicenses() {
        licenses.clear();
        try {
            LicenseDao dao = new LicenseDao();
            licenses.addAll(dao.findAll());
        } catch (SQLException e) {
            System.out.println("Error loading licenses from DB: " + e.getMessage());
        }
    }

    public static void saveData() {
        try {
            saveViolations();
            saveUsers();
            saveLicenses();
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static void saveUsers() throws IOException {
        try {
            UserDao dao = new UserDao();
            dao.replaceAll(users);
        } catch (SQLException e) {
            throw new IOException("Error saving users to DB: " + e.getMessage(), e);
        }
    }

    public static void saveLicenses() throws IOException {
        try {
            LicenseDao dao = new LicenseDao();
            dao.replaceAll(licenses);
        } catch (SQLException e) {
            throw new IOException("Error saving licenses to DB: " + e.getMessage(), e);
        }
    }

    private static void displayAllViolations() {
        if (violations.isEmpty()) {
            System.out.println("No violations found.");
            return;
        }

        for (Violation v : violations) {
            System.out.println(v);
        }
    }

    private static void searchViolations() {
        System.out.println("\n=== Search Violations ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by License ID");
        System.out.println("3. Search by Plate Number");
        System.out.println("4. Search by Violation ID");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            String searchTerm = "";
            
            switch (choice) {
                case 1:
                    searchTerm = getInput("Enter violator name");
                    if (searchTerm == null) return;
                    searchViolationsByField("name", searchTerm);
                    break;
                case 2:
                    searchTerm = getInput("Enter license ID");
                    if (searchTerm == null) return;
                    searchViolationsByField("license", searchTerm);
                    break;
                case 3:
                    searchTerm = getInput("Enter plate number");
                    if (searchTerm == null) return;
                    searchViolationsByPlate(searchTerm.toUpperCase());
                    break;
                case 4:
                    searchTerm = getInput("Enter violation ID");
                    if (searchTerm == null) return;
                    searchViolationsByField("id", searchTerm);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void searchViolationsByField(String field, String searchTerm) {
        boolean found = false;
        for (Violation v : violations) {
            boolean matches = switch (field) {
                case "name" -> v.getViolatorName().equalsIgnoreCase(searchTerm);
                case "license" -> v.getLicenseNumber() != null && v.getLicenseNumber().equals(searchTerm);
                case "id" -> String.valueOf(v.getId()).equals(searchTerm);
                default -> false;
            };
            if (matches) {
                System.out.println(v);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching violations found.");
        }
    }

    private static void searchViolationsByPlate(String plateNumber) {
        if (!AUTOMOBILE_PLATE_PATTERN.matcher(plateNumber).matches() && 
            !MOTORCYCLE_PLATE_PATTERN.matcher(plateNumber).matches()) {
            System.out.println("Invalid plate number format. Please use format:");
            System.out.println("Automobile: ABC-1234");
            System.out.println("Motorcycle: AB-12345");
            return;
        }

        User plateOwner = null;
        for (User user : users) {
            if (user.getPlateNumbers().contains(plateNumber)) {
                plateOwner = user;
                break;
            }
        }

        if (plateOwner == null) {
            System.out.println("No registered user found for this plate number.");
            return;
        }

        searchViolationsByField("name", plateOwner.getFullName());
    }

    private static void deleteViolation() {
        // Reload violations to get the latest data
        loadViolations();
        
        // Ask for license number
        String licenseNumber = getInput("Enter driver's license number");
        if (licenseNumber == null) return;
        licenseNumber = licenseNumber.toUpperCase().trim();

        // Find all violations for this license number
        List<Violation> driverViolations = new ArrayList<>();
        for (Violation v : violations) {
            if (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(licenseNumber)) {
                driverViolations.add(v);
            }
        }

        if (driverViolations.isEmpty()) {
            System.out.println("No violations found for license number: " + licenseNumber);
            return;
        }

        // Display all violations for this driver
        System.out.println("\nViolations for license number " + licenseNumber + ":");
        for (int i = 0; i < driverViolations.size(); i++) {
            Violation v = driverViolations.get(i);
            System.out.println("\n" + (i + 1) + ". " + v);
        }

        // Let officer choose which violation to delete
        Integer choice = getIntInput("\nEnter violation number to delete (1-" + driverViolations.size() + ")");
        if (choice == null || choice < 1 || choice > driverViolations.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Violation violationToDelete = driverViolations.get(choice - 1);

        // Confirm deletion
        Boolean confirm = getBooleanInput("Are you sure you want to delete this violation? (yes/no)");
        if (confirm == null || !confirm) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            // Remove violation
            violations.remove(violationToDelete);
            saveViolations();
            
            // Update license if exists
            for (DriversLicense license : licenses) {
                if (license.getLicenseNumber().equals(licenseNumber)) {
                    license.getViolations().remove(violationToDelete);
                    if (!violationToDelete.isPaid()) {
                        license.totalUnpaidFines -= violationToDelete.getFine();
                    }
                    break;
                }
            }
            saveLicenses();
            
            System.out.println("Violation deleted successfully!");
        } catch (IOException e) {
            System.out.println("Error saving changes: " + e.getMessage());
        }
    }

    public static DriversLicense findLicense(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return null;
        }
        
        String normalizedLicense = licenseNumber.trim().toUpperCase();
        for (DriversLicense license : licenses) {
            if (license.getLicenseNumber().equalsIgnoreCase(normalizedLicense)) {
                return license;
            }
        }
        return null;
    }

    private static void searchUsers() {
        System.out.println("\n=== Search Users ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by License ID");
        System.out.println("3. Search by Plate Number");
        System.out.println("4. Back");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            String searchTerm;
            
            switch (choice) {
                case 1:
                    searchTerm = getInput("Enter name to search");
                    if (searchTerm == null) return;
                    searchUsersByName(searchTerm);
                    break;
                case 2:
                    searchTerm = getInput("Enter license ID to search");
                    if (searchTerm == null) return;
                    searchUsersByLicense(searchTerm);
                    break;
                case 3:
                    searchTerm = getInput("Enter plate number to search");
                    if (searchTerm == null) return;
                    searchUsersByPlate(searchTerm);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void searchUsersByName(String searchTerm) {
        List<User> matches = new ArrayList<>();
        String searchLower = searchTerm.toLowerCase();
        
        for (User user : users) {
            if (user.getFullName().toLowerCase().contains(searchLower)) {
                matches.add(user);
            }
        }
        
        if (matches.isEmpty()) {
            System.out.println("No users found matching: " + searchTerm);
            return;
        }
        
        System.out.println("\nFound " + matches.size() + " matching users:");
        for (User user : matches) {
            System.out.println("\nUser Information:");
            System.out.println("Name: " + user.getFullName());
            System.out.println("Role: " + user.getRole());
            System.out.println("Email: " + user.getEmail());
            if (user.getRole() == Role.DRIVER) {
                System.out.println("License ID: " + (user.getLicenseId() != null ? user.getLicenseId() : "Not registered"));
                System.out.println("Registered Plates: " + (user.getPlateNumbers().isEmpty() ? "None" : String.join(", ", user.getPlateNumbers())));
            }
        }
    }

    private static void searchUsersByLicense(String licenseId) {
        User found = null;
        for (User user : users) {
            if (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(licenseId)) {
                found = user;
                break;
            }
        }
        
        if (found == null) {
            System.out.println("No user found with license ID: " + licenseId);
            return;
        }
        
        System.out.println("\nUser Information:");
        System.out.println("Name: " + found.getFullName());
        System.out.println("Role: " + found.getRole());
        System.out.println("Email: " + found.getEmail());
        System.out.println("License ID: " + found.getLicenseId());
        System.out.println("Registered Plates: " + (found.getPlateNumbers().isEmpty() ? "None" : String.join(", ", found.getPlateNumbers())));
    }

    private static void searchUsersByPlate(String plateNumber) {
        List<User> matches = new ArrayList<>();
        String plateUpper = plateNumber.toUpperCase();
        
        for (User user : users) {
            if (user.getPlateNumbers().contains(plateUpper)) {
                matches.add(user);
            }
        }
        
        if (matches.isEmpty()) {
            System.out.println("No users found with plate number: " + plateNumber);
            return;
        }
        
        System.out.println("\nFound " + matches.size() + " users with plate " + plateNumber + ":");
        for (User user : matches) {
            System.out.println("\nUser Information:");
            System.out.println("Name: " + user.getFullName());
            System.out.println("Role: " + user.getRole());
            System.out.println("Email: " + user.getEmail());
            System.out.println("License ID: " + (user.getLicenseId() != null ? user.getLicenseId() : "Not registered"));
        }
    }

    private static void removeDriverViolation() {
        System.out.println("\n=== Remove Driver Violation ===");
        
        // First, get the driver
        String searchTerm = getInput("Enter driver's name or license ID");
        if (searchTerm == null) return;
        
        User driver = null;
        for (User user : users) {
            if (user.getRole() == Role.DRIVER && 
                (user.getFullName().equalsIgnoreCase(searchTerm) || 
                 (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(searchTerm)))) {
                driver = user;
                break;
            }
        }
        
        if (driver == null) {
            System.out.println("Driver not found.");
            return;
        }
        
        // Get all violations for this driver
        List<Violation> driverViolations = new ArrayList<>();
        for (Violation v : violations) {
            if (v.getViolatorName().equalsIgnoreCase(driver.getFullName())) {
                driverViolations.add(v);
            }
        }
        
        if (driverViolations.isEmpty()) {
            System.out.println("No violations found for this driver.");
            return;
        }
        
        // Display violations
        System.out.println("\nViolations for " + driver.getFullName() + ":");
        for (int i = 0; i < driverViolations.size(); i++) {
            System.out.println((i + 1) + ". " + driverViolations.get(i));
        }
        
        // Get violation to remove
        Integer choice = getIntInput("Enter violation number to remove (1-" + driverViolations.size() + ")");
        if (choice == null || choice < 1 || choice > driverViolations.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Violation violationToRemove = driverViolations.get(choice - 1);
        
        // Confirm removal
        Boolean confirm = getBooleanInput("Are you sure you want to remove this violation? (yes/no)");
        if (confirm == null || !confirm) {
            System.out.println("Violation removal cancelled.");
            return;
        }
        
        try {
            // Remove violation
            violations.remove(violationToRemove);
            saveViolations();
            
            // Update license if exists
            if (driver.getLicenseId() != null) {
                for (DriversLicense license : licenses) {
                    if (license.getLicenseNumber().equals(driver.getLicenseId())) {
                        license.getViolations().remove(violationToRemove);
                        if (!violationToRemove.isPaid()) {
                            license.totalUnpaidFines -= violationToRemove.getFine();
                        }
                        break;
                    }
                }
                saveLicenses();
            }
            
            System.out.println("Violation removed successfully!");
        } catch (IOException e) {
            System.out.println("Error saving changes: " + e.getMessage());
        }
    }

    // --- Java Swing GUI Integration ---
        // Adds a new officer via console
        private static void addNewOfficer() {
            System.out.println("\n=== Add New Officer ===");
            String fullName = getInput("Enter officer's full name");
            if (fullName == null) return;
            String password = getInput("Enter password (6-20 characters)");
            if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
                System.out.println("Invalid password format.");
                return;
            }
            String email = getInput("Enter email");
            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format.");
                return;
            }
            User newOfficer = new User(fullName, password, email, Role.OFFICER, null);
            users.add(newOfficer);
            try {
                saveUsers();
                System.out.println("New officer added successfully!");
            } catch (IOException e) {
                System.out.println("Error saving officer: " + e.getMessage());
            }
        }
    public static class LTOSystemGUI extends JFrame {
        // Navigation history stack to track opened pages
        private final java.util.Stack<JFrame> navigationStack = new java.util.Stack<>();
        private JFrame currentFrame = null; // Track current active frame
        private final JFrame welcomeFrame; // Reference to welcome frame for logout
        
        // Modern UI Color Scheme
        private static final Color DARK_BLUE = new Color(30, 41, 59); // #1e293b
        private static final Color DARK_BLUE_LIGHT = new Color(51, 65, 85); // #334155
        private static final Color PRIMARY_BLUE = new Color(59, 130, 246); // #3b82f6
        private static final Color PRIMARY_BLUE_HOVER = new Color(37, 99, 235); // #2563eb
        private static final Color WHITE = Color.WHITE;
        private static final Color TEXT_LIGHT = new Color(241, 245, 249); // #f1f5f9
        private static final Color CARD_BG = new Color(51, 65, 85); // #334155
        private static final Color YELLOW_STATUS = new Color(251, 191, 36); // #fbbf24
        private static final Color GREEN_STATUS = new Color(34, 197, 94); // #22c55e
        private static final Color INPUT_BG = new Color(51, 65, 85); // #334155
        private static final Color INPUT_BORDER = new Color(100, 116, 139); // #64748b
        
        public LTOSystemGUI() {
            setTitle("LTO Management System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 700);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            welcomeFrame = this; // Store reference to welcome frame
            currentFrame = this; // Set welcome page as current frame
            setupDarkThemeForDialogs(); // Configure JOptionPane to match dark theme
            showWelcomePage();
            setVisible(true); // Ensure window is shown
        }
        
        /**
         * Configure UIManager settings for JOptionPane to match dark theme
         */
        private void setupDarkThemeForDialogs() {
            // Panel background
            UIManager.put("OptionPane.background", DARK_BLUE);
            UIManager.put("Panel.background", DARK_BLUE);
            
            // Button colors
            UIManager.put("Button.background", PRIMARY_BLUE);
            UIManager.put("Button.foreground", WHITE);
            UIManager.put("Button.select", PRIMARY_BLUE_HOVER);
            
            // Text colors
            UIManager.put("OptionPane.messageForeground", TEXT_LIGHT);
            UIManager.put("Label.foreground", TEXT_LIGHT);
            
            // Input field colors
            UIManager.put("TextField.background", INPUT_BG);
            UIManager.put("TextField.foreground", WHITE);
            UIManager.put("TextField.caretForeground", WHITE);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            // Password field colors
            UIManager.put("PasswordField.background", INPUT_BG);
            UIManager.put("PasswordField.foreground", WHITE);
            UIManager.put("PasswordField.caretForeground", WHITE);
            UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            // Option pane border
            UIManager.put("OptionPane.border", BorderFactory.createLineBorder(INPUT_BORDER, 1));
            
            // Message area border
            UIManager.put("OptionPane.messageAreaBorder", BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Button area border
            UIManager.put("OptionPane.buttonAreaBorder", BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        /**
         * Create a modern styled button with blue background
         */
        private JButton createModernButton(String text, int width, int height) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(width, height));
            btn.setMaximumSize(new Dimension(width, height));
            btn.setBackground(PRIMARY_BLUE);
            btn.setForeground(WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(PRIMARY_BLUE_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(PRIMARY_BLUE);
                }
            });
            return btn;
        }
        
        /**
         * Create a sidebar navigation button with icon
         */
        private JButton createSidebarButton(String icon, String text, int width, Color bgColor) {
            JButton btn = new JButton("<html><div style='text-align: left; padding-left: 10px;'><span style='font-size: 16px;'>" + 
                icon + "</span>  <span style='font-size: 14px;'>" + text + "</span></div></html>");
            btn.setPreferredSize(new Dimension(width, 50));
            btn.setMaximumSize(new Dimension(width, 50));
            btn.setBackground(bgColor);
            btn.setForeground(WHITE);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (bgColor == PRIMARY_BLUE) {
                        btn.setBackground(PRIMARY_BLUE_HOVER);
                    } else {
                        btn.setBackground(new Color(Math.min(bgColor.getRed() + 20, 255), 
                            Math.min(bgColor.getGreen() + 20, 255), 
                            Math.min(bgColor.getBlue() + 20, 255)));
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(bgColor);
                }
            });
            return btn;
        }
        
        /**
         * Create a modern styled input field
         */
        private JTextField createModernInputField(int width, int height) {
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(width, height));
            field.setMaximumSize(new Dimension(width, height));
            field.setBackground(INPUT_BG);
            field.setForeground(WHITE);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            return field;
        }
        
        /**
         * Create a modern styled password field
         */
        private JPasswordField createModernPasswordField(int width, int height) {
            JPasswordField field = new JPasswordField();
            field.setPreferredSize(new Dimension(width, height));
            field.setMaximumSize(new Dimension(width, height));
            field.setBackground(INPUT_BG);
            field.setForeground(WHITE);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            return field;
        }
        
        /**
         * Create a labeled input field container with label clearly above the input
         * Uses GridBagLayout for proper positioning
         */
        private JPanel createLabeledInputField(String labelText, JComponent inputField, int width) {
            JPanel container = new JPanel();
            container.setLayout(new GridBagLayout());
            container.setBackground(DARK_BLUE);
            container.setPreferredSize(new Dimension(width, 70));
            container.setMaximumSize(new Dimension(width, 70));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);
            
            // Label in first row - clearly above the input field
            JLabel label = createStyledLabel(labelText, 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 10, 0); // 10px spacing below label
            container.add(label, gbc);
            
            // Input field in second row - below the label with clear separation
            inputField.setPreferredSize(new Dimension(width, 45));
            inputField.setMaximumSize(new Dimension(width, 45));
            inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 8, 12)
            ));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 0, 0); // No extra spacing for input field
            gbc.fill = GridBagConstraints.HORIZONTAL;
            container.add(inputField, gbc);
            
            return container;
        }
        
        /**
         * Create a panel with dark blue background
         */
        private JPanel createDarkPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(DARK_BLUE);
            return panel;
        }
        
        /**
         * Create a styled label
         */
        private JLabel createStyledLabel(String text, Font font, Color color) {
            JLabel label = new JLabel(text);
            label.setFont(font);
            label.setForeground(color);
            return label;
        }
        
        /**
         * Create a violation card panel with status color
         */
        private JPanel createViolationCard(LTOSystem.Violation violation, int width) {
            JPanel card = new JPanel();
            card.setPreferredSize(new Dimension(width, 80));
            card.setMaximumSize(new Dimension(width, 80));
            card.setLayout(new BorderLayout(15, 10));
            card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            
            // Determine card background color based on status
            Color cardBg;
            if (violation.isPaid()) {
                cardBg = GREEN_STATUS;
            } else if (violation.isApproved()) {
                cardBg = new Color(59, 130, 246); // Blue for approved
            } else if (violation.isRejected()) {
                cardBg = new Color(220, 38, 38); // Red for rejected
            } else {
                cardBg = YELLOW_STATUS; // Yellow for pending
            }
            card.setBackground(cardBg);
            
            // Violation info
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(cardBg);
            infoPanel.setOpaque(false);
            
            String violationName = violation.getType().getDescription();
            if (violationName.length() > 30) {
                violationName = violationName.substring(0, 27) + "...";
            }
            JLabel nameLabel = createStyledLabel(violationName, 
                new Font("Segoe UI", Font.BOLD, 13), WHITE);
            infoPanel.add(nameLabel);
            
            JLabel fineLabel = createStyledLabel("₱" + String.format("%,.2f", violation.getFine()), 
                new Font("Segoe UI", Font.PLAIN, 11), WHITE);
            infoPanel.add(fineLabel);
            
            // Status label
            String status = violation.getStatusString();
            JLabel statusLabel = createStyledLabel("Status: " + status, 
                new Font("Segoe UI", Font.PLAIN, 10), WHITE);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            infoPanel.add(statusLabel);
            
            card.add(infoPanel, BorderLayout.CENTER);
            return card;
        }
        
        /**
         * Navigate to a new page by hiding current frame and showing new one
         * Pushes current frame to navigation stack before navigating (frames are hidden, not disposed, so they can be restored)
         */
        private void navigateToPage(JFrame newFrame) {
            if (currentFrame != null && currentFrame.isDisplayable()) {
                // Push current frame to navigation stack (will be hidden, not disposed)
                navigationStack.push(currentFrame);
                // Hide current frame (don't dispose - we need it for back navigation)
                currentFrame.setVisible(false);
            }
            currentFrame = newFrame;
            newFrame.setVisible(true);
            newFrame.toFront();
            newFrame.requestFocus();
        }
        
        /**
         * Navigate back to previous page using navigation history
         * Restores previous frame from stack
         */
        private void navigateBack() {
            if (!navigationStack.isEmpty()) {
                // Hide current frame (don't dispose yet - let it be garbage collected naturally if not referenced)
                if (currentFrame != null && currentFrame.isDisplayable() && currentFrame != welcomeFrame) {
                    currentFrame.setVisible(false);
                    // Dispose non-welcome frames to prevent memory leaks
                    currentFrame.dispose();
                } else if (currentFrame != null && currentFrame.isDisplayable()) {
                    currentFrame.setVisible(false);
                }
                // Restore previous frame from stack
                currentFrame = navigationStack.pop();
                currentFrame.setVisible(true);
                currentFrame.toFront();
                currentFrame.requestFocus();
            }
        }
        
        /**
         * Navigate back to welcome page (used for logout)
         * Disposes all frames in history and returns to welcome screen
         */
        private void navigateToWelcome() {
            // Dispose current frame if it's not the welcome frame
            if (currentFrame != null && currentFrame.isDisplayable() && currentFrame != welcomeFrame) {
                currentFrame.setVisible(false);
                currentFrame.dispose();
            }
            // Dispose all frames in navigation history to prevent memory leaks
            while (!navigationStack.isEmpty()) {
                JFrame frame = navigationStack.pop();
                if (frame.isDisplayable() && frame != welcomeFrame) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            }
            // Show welcome frame
            currentFrame = welcomeFrame;
            welcomeFrame.setVisible(true);
            welcomeFrame.toFront();
            welcomeFrame.requestFocus();
        }
        
        /**
         * Close current frame without navigation (used for exit)
         */
        private void closeCurrentFrame() {
            if (currentFrame != null && currentFrame.isDisplayable()) {
                currentFrame.dispose();
            }
            // Dispose all frames in navigation stack
            while (!navigationStack.isEmpty()) {
                JFrame frame = navigationStack.pop();
                if (frame.isDisplayable()) {
                    frame.dispose();
                }
            }
        }

        private void showWelcomePage() {
            getContentPane().removeAll();
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            
            // Top padding
            mainPanel.add(Box.createVerticalStrut(40), BorderLayout.NORTH);
            
            // Center content panel
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(DARK_BLUE);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
            centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Title
            JLabel titleLabel = createStyledLabel("Welcome to LTO System", 
                new Font("Segoe UI", Font.BOLD, 32), WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(titleLabel);
            
            // Subtitle
            JLabel subtitleLabel = createStyledLabel("Sign in to continue", 
                new Font("Segoe UI", Font.PLAIN, 16), TEXT_LIGHT);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
            centerPanel.add(subtitleLabel);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            
            JButton loginBtn = createModernButton("LOGIN", 200, 45);
            loginBtn.addActionListener(e -> showLoginPage());
            buttonPanel.add(loginBtn);
            
            JButton registerBtn = createModernButton("REGISTER", 200, 45);
            registerBtn.setBackground(DARK_BLUE_LIGHT);
            registerBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    registerBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    registerBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            registerBtn.addActionListener(e -> showRegisterPage());
            buttonPanel.add(registerBtn);
            
            centerPanel.add(buttonPanel);
            
            // Bottom padding
            mainPanel.add(Box.createVerticalStrut(40), BorderLayout.SOUTH);
            
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            setContentPane(mainPanel);
            getContentPane().setBackground(DARK_BLUE);
            revalidate();
            repaint();
        }

        private void showRegisterPage() {
            JFrame regFrame = new JFrame("Driver Registration - LTO System");
            regFrame.setSize(500, 750);
            regFrame.setLocationRelativeTo(null);
            regFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Driver Registration", 
                new Font("Segoe UI", Font.BOLD, 28), WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titlePanel.add(titleLabel);
            
            JLabel subtitleLabel = createStyledLabel("Create your account", 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
            titlePanel.add(subtitleLabel);
            
            // Form panel with scroll support
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(DARK_BLUE);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Full Name field
            JTextField nameField = new JTextField();
            nameField.setBackground(INPUT_BG);
            nameField.setForeground(WHITE);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JPanel nameContainer = createLabeledInputField("Full Name", nameField, 380);
            nameContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(nameContainer);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Password field
            JPasswordField passField = new JPasswordField();
            passField.setBackground(INPUT_BG);
            passField.setForeground(WHITE);
            passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            passField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JPanel passContainer = createLabeledInputField("Password (6-20 characters)", passField, 380);
            passContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(passContainer);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Email field
            JTextField emailField = new JTextField();
            emailField.setBackground(INPUT_BG);
            emailField.setForeground(WHITE);
            emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emailField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JPanel emailContainer = createLabeledInputField("Email", emailField, 380);
            emailContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(emailContainer);
            formPanel.add(Box.createVerticalStrut(20));
            
            // License ID field
            JTextField licenseField = new JTextField();
            licenseField.setBackground(INPUT_BG);
            licenseField.setForeground(WHITE);
            licenseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            licenseField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            licenseField.setText("A01-23-456789");
            JPanel licenseContainer = createLabeledInputField("License ID (Format: A01-23-456789)", licenseField, 380);
            licenseContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Auto-format license ID as A01-23-456789
            licenseField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    String text = licenseField.getText().replaceAll("[^A-Za-z0-9]", "");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < text.length() && i < 11; i++) {
                        sb.append(text.charAt(i));
                        if (i == 2 || i == 4) sb.append('-');
                    }
                    licenseField.setText(sb.toString());
                }
            });
            formPanel.add(licenseContainer);
            formPanel.add(Box.createVerticalStrut(40));
            
            // Register button
            JButton submitBtn = createModernButton("REGISTER", 380, 50);
            submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(submitBtn);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Back button
            JButton backBtn = createModernButton("← Back", 150, 35);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> navigateBack());
            formPanel.add(backBtn);
            
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            regFrame.setContentPane(mainPanel);
            navigateToPage(regFrame); // Use navigation system
            submitBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String pass = new String(passField.getPassword());
                String email = emailField.getText().trim();
                String licenseId = licenseField.getText().trim().toUpperCase();
                // Name validation: only letters and spaces
                if (!name.matches("^[\\p{L}Ññ ]+$")) {
                    JOptionPane.showMessageDialog(regFrame, "Name must contain only letters and spaces.");
                    nameField.setText("");
                    nameField.requestFocus();
                    return;
                }
                // Password validation
                if (!pass.matches("^[A-Za-z0-9@#$%^&+=]{6,20}$")) {
                    JOptionPane.showMessageDialog(regFrame, "Password must be 6-20 characters and valid symbols.");
                    passField.setText("");
                    passField.requestFocus();
                    return;
                }
                // Email validation
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    JOptionPane.showMessageDialog(regFrame, "Invalid email format.");
                    emailField.setText("");
                    emailField.requestFocus();
                    return;
                }
                // License ID duplicate check
                boolean exists = false;
                for (LTOSystem.User u : LTOSystem.users) {
                    if (u.getLicenseId() != null && u.getLicenseId().equalsIgnoreCase(licenseId)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    JOptionPane.showMessageDialog(regFrame, "This License ID is already registered to another user.");
                    licenseField.setText("");
                    licenseField.requestFocus();
                    return;
                }
                try {
                    LTOSystem.DriversLicense license = new LTOSystem.DriversLicense(licenseId, name, "N/A", new java.util.Date());
                    LTOSystem.User newUser = new LTOSystem.User(name, pass, email, LTOSystem.Role.DRIVER, licenseId);
                    LTOSystem.users.add(newUser);
                    LTOSystem.licenses.add(license);
                    LTOSystem.saveUsers();
                    LTOSystem.saveLicenses();
                    JOptionPane.showMessageDialog(regFrame, "Registration successful!\nLicense Number: " + licenseId);
                    navigateBack(); // Navigate back to welcome page after successful registration
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(regFrame, "Registration failed: " + ex.getMessage());
                }
            });
            backBtn.addActionListener(e -> navigateBack()); // Navigate back using navigation history
        }

        private void showLoginPage() {
            JFrame loginFrame = new JFrame("Login - LTO System");
            loginFrame.setSize(500, 700);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Welcome to LTO System", 
                new Font("Segoe UI", Font.BOLD, 28), WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titlePanel.add(titleLabel);
            
            JLabel subtitleLabel = createStyledLabel("Sign in to continue", 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
            titlePanel.add(subtitleLabel);
            
            // Form panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(DARK_BLUE);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Full Name field
            JTextField nameField = new JTextField();
            nameField.setBackground(INPUT_BG);
            nameField.setForeground(WHITE);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JPanel nameContainer = createLabeledInputField("Full Name", nameField, 380);
            nameContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(nameContainer);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Password field
            JPasswordField passField = new JPasswordField();
            passField.setBackground(INPUT_BG);
            passField.setForeground(WHITE);
            passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            passField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JPanel passContainer = createLabeledInputField("Password", passField, 380);
            passContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(passContainer);
            formPanel.add(Box.createVerticalStrut(30));
            
            // Role selection section - properly centered container
            JPanel roleSectionPanel = new JPanel();
            roleSectionPanel.setLayout(new BoxLayout(roleSectionPanel, BoxLayout.Y_AXIS));
            roleSectionPanel.setBackground(DARK_BLUE);
            roleSectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // "Select Role" label - centered
            JLabel roleLabel = createStyledLabel("Select Role", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            roleSectionPanel.add(roleLabel);
            
            // Role buttons panel - centered with equal spacing
            JPanel rolePanel = new JPanel();
            rolePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            rolePanel.setBackground(DARK_BLUE);
            rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton driverRoleBtn = createModernButton("Driver", 110, 40);
            JButton officerRoleBtn = createModernButton("Officer", 110, 40);
            JButton adminRoleBtn = createModernButton("Admin", 110, 40);
            
            final String[] selectedRole = {""};
            java.awt.event.ActionListener roleListener = e -> {
                JButton clicked = (JButton) e.getSource();
                selectedRole[0] = clicked.getText().toUpperCase();
                // Reset all buttons
                driverRoleBtn.setBackground(DARK_BLUE_LIGHT);
                officerRoleBtn.setBackground(DARK_BLUE_LIGHT);
                adminRoleBtn.setBackground(DARK_BLUE_LIGHT);
                // Highlight selected
                clicked.setBackground(PRIMARY_BLUE);
            };
            driverRoleBtn.addActionListener(roleListener);
            officerRoleBtn.addActionListener(roleListener);
            adminRoleBtn.addActionListener(roleListener);
            driverRoleBtn.setBackground(DARK_BLUE_LIGHT);
            officerRoleBtn.setBackground(DARK_BLUE_LIGHT);
            adminRoleBtn.setBackground(DARK_BLUE_LIGHT);
            
            rolePanel.add(driverRoleBtn);
            rolePanel.add(officerRoleBtn);
            rolePanel.add(adminRoleBtn);
            roleSectionPanel.add(rolePanel);
            
            // Add the entire role section to form panel
            formPanel.add(roleSectionPanel);
            formPanel.add(Box.createVerticalStrut(40));
            
            // Login button
            JButton loginBtn = createModernButton("LOGIN", 380, 50);
            loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String pass = new String(passField.getPassword());
                if (selectedRole[0].isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "Please select a role.");
                    return;
                }
                LTOSystem.Role role = LTOSystem.Role.valueOf(selectedRole[0]);
                for (LTOSystem.User user : LTOSystem.users) {
                    if (user.getFullName().equalsIgnoreCase(name) && user.getPassword().equals(pass) && user.getRole() == role) {
                        showMenuForRole(user);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials or insufficient privileges.");
            });
            formPanel.add(loginBtn);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Signup link
            JLabel signupLabel = createStyledLabel("Don't have an account? Signup", 
                new Font("Segoe UI", Font.PLAIN, 12), PRIMARY_BLUE);
            signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    navigateBack();
                    showRegisterPage();
                }
            });
            formPanel.add(signupLabel);
            
            // Back button
            JButton backBtn = createModernButton("← Back", 150, 35);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> navigateBack());
            formPanel.add(Box.createVerticalStrut(20));
            formPanel.add(backBtn);
            
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            loginFrame.setContentPane(mainPanel);
            navigateToPage(loginFrame);
        }

        private void showMenuForRole(LTOSystem.User user) {
            switch (user.getRole()) {
                case ADMIN -> showAdminMenuGUI(user);
                case OFFICER -> showOfficerMenuGUI(user);
                case DRIVER -> showDriverMenuGUI(user);
            }
        }

        private void showAdminMenuGUI(LTOSystem.User user) {
            JFrame adminFrame = new JFrame("Admin Menu");
            adminFrame.setSize(1200, 800);
            adminFrame.setLocationRelativeTo(null);
            adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Main panel with BorderLayout
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            
            // Left sidebar navigation panel
            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setBackground(DARK_BLUE_LIGHT);
            sidebarPanel.setPreferredSize(new Dimension(320, 0));
            sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // Welcome message in sidebar
            JLabel welcomeLabel = createStyledLabel("Welcome, " + user.getFullName(), 
                new Font("Segoe UI", Font.BOLD, 20), WHITE);
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
            sidebarPanel.add(welcomeLabel);
            
            // Admin badge/role indicator
            JLabel roleLabel = createStyledLabel("👑 Administrator", 
                new Font("Segoe UI", Font.PLAIN, 14), new Color(251, 191, 36));
            roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
            sidebarPanel.add(roleLabel);
            sidebarPanel.add(Box.createVerticalStrut(10));
            
            // Navigation buttons with icons
            int sidebarBtnWidth = 300;
            JButton viewUsersBtn = createSidebarButton("👥", "View All Users", sidebarBtnWidth, PRIMARY_BLUE);
            JButton searchUsersBtn = createSidebarButton("🔍", "Search Users", sidebarBtnWidth, PRIMARY_BLUE);
            JButton approveViolationsBtn = createSidebarButton("✅", "Approve Violations", sidebarBtnWidth, PRIMARY_BLUE);
            JButton removeViolationBtn = createSidebarButton("⚠️", "Remove Driver Violation", sidebarBtnWidth, PRIMARY_BLUE);
            JButton addOfficerBtn = createSidebarButton("➕", "Add New Officer", sidebarBtnWidth, PRIMARY_BLUE);
            
            // Logout button with red color
            Color logoutRed = new Color(220, 38, 38);
            JButton logoutBtn = createSidebarButton("🚪", "Logout", sidebarBtnWidth, logoutRed);
            logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(new Color(Math.min(logoutRed.getRed() + 20, 255), 
                        logoutRed.getGreen(), logoutRed.getBlue()));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(logoutRed);
                }
            });
            
            // Add buttons to sidebar with spacing
            sidebarPanel.add(viewUsersBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(searchUsersBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(approveViolationsBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(removeViolationBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(addOfficerBtn);
            sidebarPanel.add(Box.createVerticalGlue()); // Push logout to bottom
            sidebarPanel.add(logoutBtn);
            
            // Right content area with CardLayout for dynamic content switching
            JPanel contentPanel = createDarkPanel();
            CardLayout cardLayout = new CardLayout();
            contentPanel.setLayout(cardLayout);
            
            // Create all view panels
            JPanel welcomePanel = createAdminWelcomePanel(user);
            final JPanel[] viewUsersPanelRef = {createViewAllUsersPanel(adminFrame, cardLayout, contentPanel)};
            final JPanel[] searchUsersPanelRef = {createSearchUsersPanel(adminFrame, cardLayout, contentPanel)};
            final JPanel[] approveViolationsPanelRef = {createApproveViolationsPanel(adminFrame, cardLayout, contentPanel)};
            final JPanel[] removeViolationPanelRef = {createRemoveViolationPanel(adminFrame, cardLayout, contentPanel)};
            final JPanel[] addOfficerPanelRef = {createAddOfficerPanel(adminFrame, cardLayout, contentPanel)};
            
            // Add all panels to CardLayout with unique names
            contentPanel.add(welcomePanel, "WELCOME");
            contentPanel.add(viewUsersPanelRef[0], "VIEW_USERS");
            contentPanel.add(searchUsersPanelRef[0], "SEARCH_USERS");
            contentPanel.add(approveViolationsPanelRef[0], "APPROVE_VIOLATIONS");
            contentPanel.add(removeViolationPanelRef[0], "REMOVE_VIOLATION");
            contentPanel.add(addOfficerPanelRef[0], "ADD_OFFICER");
            
            // Show welcome panel by default
            cardLayout.show(contentPanel, "WELCOME");
            
            // Add action listeners to switch cards
            viewUsersBtn.addActionListener(e -> {
                contentPanel.remove(viewUsersPanelRef[0]);
                viewUsersPanelRef[0] = createViewAllUsersPanel(adminFrame, cardLayout, contentPanel);
                contentPanel.add(viewUsersPanelRef[0], "VIEW_USERS");
                cardLayout.show(contentPanel, "VIEW_USERS");
            });
            searchUsersBtn.addActionListener(e -> {
                contentPanel.remove(searchUsersPanelRef[0]);
                searchUsersPanelRef[0] = createSearchUsersPanel(adminFrame, cardLayout, contentPanel);
                contentPanel.add(searchUsersPanelRef[0], "SEARCH_USERS");
                cardLayout.show(contentPanel, "SEARCH_USERS");
            });
            approveViolationsBtn.addActionListener(e -> {
                contentPanel.remove(approveViolationsPanelRef[0]);
                approveViolationsPanelRef[0] = createApproveViolationsPanel(adminFrame, cardLayout, contentPanel);
                contentPanel.add(approveViolationsPanelRef[0], "APPROVE_VIOLATIONS");
                cardLayout.show(contentPanel, "APPROVE_VIOLATIONS");
            });
            removeViolationBtn.addActionListener(e -> {
                contentPanel.remove(removeViolationPanelRef[0]);
                removeViolationPanelRef[0] = createRemoveViolationPanel(adminFrame, cardLayout, contentPanel);
                contentPanel.add(removeViolationPanelRef[0], "REMOVE_VIOLATION");
                cardLayout.show(contentPanel, "REMOVE_VIOLATION");
            });
            addOfficerBtn.addActionListener(e -> {
                contentPanel.remove(addOfficerPanelRef[0]);
                addOfficerPanelRef[0] = createAddOfficerPanel(adminFrame, cardLayout, contentPanel);
                contentPanel.add(addOfficerPanelRef[0], "ADD_OFFICER");
                cardLayout.show(contentPanel, "ADD_OFFICER");
            });
            logoutBtn.addActionListener(e -> navigateToWelcome());
            
            // Add panels to main layout
            mainPanel.add(sidebarPanel, BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            adminFrame.setContentPane(mainPanel);
            navigateToPage(adminFrame);
        }

        /**
         * Create the welcome panel for Admin Menu
         */
        private JPanel createAdminWelcomePanel(LTOSystem.User user) {
            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
            welcomePanel.setBackground(DARK_BLUE);
            welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel welcomeLabel = createStyledLabel("Welcome to Admin Dashboard", 
                new Font("Segoe UI", Font.BOLD, 28), TEXT_LIGHT);
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            welcomePanel.add(Box.createVerticalGlue());
            welcomePanel.add(welcomeLabel);
            
            JLabel userLabel = createStyledLabel("👤 " + user.getFullName(), 
                new Font("Segoe UI", Font.PLAIN, 18), TEXT_LIGHT);
            userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            userLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
            welcomePanel.add(userLabel);
            
            JLabel instructionLabel = createStyledLabel("Select an option from the menu to manage the system", 
                new Font("Segoe UI", Font.PLAIN, 14), new Color(148, 163, 184));
            instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            welcomePanel.add(instructionLabel);
            welcomePanel.add(Box.createVerticalGlue());
            
            return welcomePanel;
        }

        /**
         * Create the View All Users panel
         */
        private JPanel createViewAllUsersPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("All Users", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Scrollable content area
            JPanel contentArea = new JPanel();
            contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
            contentArea.setBackground(DARK_BLUE);
            
            if (LTOSystem.users.isEmpty()) {
                JLabel noUsersLabel = createStyledLabel("No users found.", 
                    new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                noUsersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noUsersLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                contentArea.add(noUsersLabel);
            } else {
                for (LTOSystem.User user : LTOSystem.users) {
                    JPanel userCard = createUserCard(user);
                    userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentArea.add(userCard);
                    contentArea.add(Box.createVerticalStrut(10));
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(contentArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(DARK_BLUE);
            scrollPane.getViewport().setBackground(DARK_BLUE);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        /**
         * Create a user card for display
         */
        private JPanel createUserCard(LTOSystem.User user) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            
            // Role badge
            String roleIcon = user.getRole() == LTOSystem.Role.ADMIN ? "👑" : 
                             user.getRole() == LTOSystem.Role.OFFICER ? "👮" : "🚗";
            Color roleColor = user.getRole() == LTOSystem.Role.ADMIN ? new Color(251, 191, 36) :
                             user.getRole() == LTOSystem.Role.OFFICER ? PRIMARY_BLUE : GREEN_STATUS;
            
            JLabel roleLabel = createStyledLabel(roleIcon + " " + user.getRole(), 
                new Font("Segoe UI", Font.BOLD, 14), roleColor);
            roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            card.add(roleLabel);
            
            JLabel nameLabel = createStyledLabel("Name: " + user.getFullName(), 
                new Font("Segoe UI", Font.PLAIN, 13), TEXT_LIGHT);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            card.add(nameLabel);
            
            JLabel emailLabel = createStyledLabel("Email: " + user.getEmail(), 
                new Font("Segoe UI", Font.PLAIN, 13), TEXT_LIGHT);
            emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            card.add(emailLabel);
            
            if (user.getRole() == LTOSystem.Role.DRIVER) {
                if (user.getLicenseId() != null) {
                    JLabel licenseLabel = createStyledLabel("License: " + user.getLicenseId(), 
                        new Font("Segoe UI", Font.PLAIN, 13), TEXT_LIGHT);
                    licenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                    card.add(licenseLabel);
                }
                if (!user.getPlateNumbers().isEmpty()) {
                    JLabel plateLabel = createStyledLabel("Plates: " + String.join(", ", user.getPlateNumbers()), 
                        new Font("Segoe UI", Font.PLAIN, 13), TEXT_LIGHT);
                    card.add(plateLabel);
                }
            }
            
            return card;
        }

        /**
         * Create the Search Users panel
         */
        private JPanel createSearchUsersPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Search Users", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            titlePanel.add(titleLabel);
            
            JLabel searchLabel = createStyledLabel("Search by name, license ID, or plate number", 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            searchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(searchLabel);
            
            // Search input area
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
            searchPanel.setBackground(DARK_BLUE);
            searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JTextField searchField = createModernInputField(400, 45);
            searchPanel.add(searchField);
            searchPanel.add(Box.createHorizontalStrut(10));
            
            JButton searchBtn = new JButton("🔍 Search");
            searchBtn.setPreferredSize(new Dimension(120, 45));
            searchBtn.setBackground(PRIMARY_BLUE);
            searchBtn.setForeground(WHITE);
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            searchBtn.setFocusPainted(false);
            searchBtn.setBorderPainted(false);
            searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    searchBtn.setBackground(PRIMARY_BLUE_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    searchBtn.setBackground(PRIMARY_BLUE);
                }
            });
            searchPanel.add(searchBtn);
            
            // Results area
            JPanel resultsPanel = new JPanel();
            resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
            resultsPanel.setBackground(DARK_BLUE);
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
            
            final JPanel[] resultsContentRef = {new JPanel()};
            resultsContentRef[0].setLayout(new BoxLayout(resultsContentRef[0], BoxLayout.Y_AXIS));
            resultsContentRef[0].setBackground(DARK_BLUE);
            
            // Initial message in results area
            JLabel initialLabel = createStyledLabel("Enter a search term and click Search to find users", 
                new Font("Segoe UI", Font.PLAIN, 14), new Color(148, 163, 184));
            initialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            initialLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            resultsContentRef[0].add(initialLabel);
            
            JScrollPane resultsScroll = new JScrollPane(resultsContentRef[0]);
            resultsScroll.setBorder(BorderFactory.createEmptyBorder());
            resultsScroll.setBackground(DARK_BLUE);
            resultsScroll.getViewport().setBackground(DARK_BLUE);
            resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            resultsScroll.setPreferredSize(new Dimension(600, 350));
            
            searchBtn.addActionListener(e -> {
                String term = searchField.getText().trim();
                resultsContentRef[0].removeAll();
                resultsContentRef[0].revalidate();
                resultsContentRef[0].repaint();
                
                if (term.isEmpty()) {
                    JLabel noTermLabel = createStyledLabel("Please enter a search term", 
                        new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                    noTermLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noTermLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                    resultsContentRef[0].add(noTermLabel);
                } else {
                    java.util.List<LTOSystem.User> matches = new java.util.ArrayList<>();
                    for (LTOSystem.User user : LTOSystem.users) {
                        if (user.getFullName().toLowerCase().contains(term.toLowerCase()) ||
                            (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(term)) ||
                            user.getPlateNumbers().contains(term.toUpperCase())) {
                            matches.add(user);
                        }
                    }
                    
                    if (matches.isEmpty()) {
                        JLabel noResultsLabel = createStyledLabel("❌ No users found matching: \"" + term + "\"", 
                            new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                        noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        noResultsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                        resultsContentRef[0].add(noResultsLabel);
                    } else {
                        JLabel resultsLabel = createStyledLabel("✓ Found " + matches.size() + " result(s):", 
                            new Font("Segoe UI", Font.BOLD, 14), GREEN_STATUS);
                        resultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        resultsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                        resultsContentRef[0].add(resultsLabel);
                        
                        for (LTOSystem.User user : matches) {
                            JPanel userCard = createUserCard(user);
                            userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                            resultsContentRef[0].add(userCard);
                            resultsContentRef[0].add(Box.createVerticalStrut(10));
                        }
                    }
                }
                
                // Force refresh and scroll to top
                resultsContentRef[0].revalidate();
                resultsContentRef[0].repaint();
                resultsScroll.getViewport().setViewPosition(new java.awt.Point(0, 0));
                
                // Ensure results panel is visible
                resultsPanel.setVisible(true);
                resultsScroll.setVisible(true);
            });
            
            // Allow Enter key to trigger search
            searchField.addActionListener(e -> searchBtn.doClick());
            
            resultsPanel.add(resultsScroll);
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.setBackground(DARK_BLUE);
            bottomPanel.add(resultsPanel);
            bottomPanel.add(buttonPanel);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(searchPanel, BorderLayout.CENTER);
            panel.add(bottomPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        /**
         * Create the Approve Violations panel
         */
        private JPanel createApproveViolationsPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Approve/Reject Violations", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            titlePanel.add(titleLabel);
            
            JLabel subtitleLabel = createStyledLabel("Review and approve or reject pending violations", 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(subtitleLabel);
            
            // Content area with violations
            JPanel contentArea = new JPanel();
            contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
            contentArea.setBackground(DARK_BLUE);
            
            // Get all PENDING violations (not approved, not rejected, not paid)
            java.util.List<LTOSystem.Violation> pendingViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (!v.isPaid() && !v.isApproved() && !v.isRejected()) {
                    pendingViolations.add(v);
                }
            }
            
            final JPanel[] violationsContentRef = {new JPanel()};
            violationsContentRef[0].setLayout(new BoxLayout(violationsContentRef[0], BoxLayout.Y_AXIS));
            violationsContentRef[0].setBackground(DARK_BLUE);
            
            if (pendingViolations.isEmpty()) {
                JLabel noPendingLabel = createStyledLabel("✓ No pending violations to review", 
                    new Font("Segoe UI", Font.PLAIN, 14), GREEN_STATUS);
                noPendingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noPendingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                violationsContentRef[0].add(noPendingLabel);
            } else {
                JLabel countLabel = createStyledLabel("Found " + pendingViolations.size() + " pending violation(s):", 
                    new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT);
                countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                countLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                violationsContentRef[0].add(countLabel);
                
                for (LTOSystem.Violation violation : pendingViolations) {
                    JPanel violationCard = createViolationApprovalCard(violation, violationsContentRef[0], parentFrame, cardLayout, contentPanel);
                    violationCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                    violationsContentRef[0].add(violationCard);
                    violationsContentRef[0].add(Box.createVerticalStrut(10));
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(violationsContentRef[0]);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(DARK_BLUE);
            scrollPane.getViewport().setBackground(DARK_BLUE);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        /**
         * Create a violation card with approve/reject buttons
         */
        private JPanel createViolationApprovalCard(LTOSystem.Violation violation, JPanel parentPanel, JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(CARD_BG);
            
            JLabel violationLabel = createStyledLabel("⚠️ " + violation.getType().getDescription(), 
                new Font("Segoe UI", Font.BOLD, 14), YELLOW_STATUS);
            violationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            infoPanel.add(violationLabel);
            
            JLabel detailsLabel = createStyledLabel("Violator: " + violation.getViolatorName() + 
                " | Fine: ₱" + String.format("%,.2f", violation.getFine()), 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            infoPanel.add(detailsLabel);
            
            if (violation.getLicenseNumber() != null && !violation.getLicenseNumber().isEmpty()) {
                JLabel licenseLabel = createStyledLabel("License: " + violation.getLicenseNumber(), 
                    new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
                licenseLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                infoPanel.add(licenseLabel);
            }
            
            JLabel idLabel = createStyledLabel("Violation ID: " + violation.getId() + 
                " | Date: " + violation.getDateIssued().toString(), 
                new Font("Segoe UI", Font.PLAIN, 11), new Color(148, 163, 184));
            idLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            infoPanel.add(idLabel);
            
            // Buttons panel
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
            buttonsPanel.setBackground(CARD_BG);
            buttonsPanel.setPreferredSize(new Dimension(140, 0));
            
            JButton approveBtn = new JButton("✅ Approve");
            approveBtn.setPreferredSize(new Dimension(120, 35));
            approveBtn.setMaximumSize(new Dimension(120, 35));
            approveBtn.setBackground(GREEN_STATUS);
            approveBtn.setForeground(WHITE);
            approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            approveBtn.setFocusPainted(false);
            approveBtn.setBorderPainted(false);
            approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            approveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    approveBtn.setBackground(new Color(34, 197, 94));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    approveBtn.setBackground(GREEN_STATUS);
                }
            });
            approveBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(parentFrame, 
                    "Approve this violation?\n\n" + violation.toString(), 
                    "Approve Violation", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    violation.setApproved(true);
                    try {
                        LTOSystem.saveViolations();
                        JOptionPane.showMessageDialog(parentFrame, "Violation approved successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the panel by removing the old one and creating a new one
                        Component[] components = contentPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JPanel) {
                                // Check if this is the approve violations panel by checking its title
                                JPanel p = (JPanel) comp;
                                if (p.getComponentCount() > 0) {
                                    Component firstComp = p.getComponent(0);
                                    if (firstComp instanceof JPanel) {
                                        JPanel titlePanel = (JPanel) firstComp;
                                        if (titlePanel.getComponentCount() > 0) {
                                            Component titleComp = titlePanel.getComponent(0);
                                            if (titleComp instanceof JLabel) {
                                                String text = ((JLabel) titleComp).getText();
                                                if (text != null && text.contains("Approve/Reject Violations")) {
                                                    contentPanel.remove(p);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        JPanel newPanel = createApproveViolationsPanel(parentFrame, cardLayout, contentPanel);
                        contentPanel.add(newPanel, "APPROVE_VIOLATIONS");
                        cardLayout.show(contentPanel, "APPROVE_VIOLATIONS");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame, "Error saving: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonsPanel.add(approveBtn);
            buttonsPanel.add(Box.createVerticalStrut(8));
            
            JButton rejectBtn = new JButton("❌ Reject");
            rejectBtn.setPreferredSize(new Dimension(120, 35));
            rejectBtn.setMaximumSize(new Dimension(120, 35));
            rejectBtn.setBackground(new Color(220, 38, 38));
            rejectBtn.setForeground(WHITE);
            rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rejectBtn.setFocusPainted(false);
            rejectBtn.setBorderPainted(false);
            rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rejectBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    rejectBtn.setBackground(new Color(185, 28, 28));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    rejectBtn.setBackground(new Color(220, 38, 38));
                }
            });
            rejectBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(parentFrame, 
                    "Reject this violation?\n\n" + violation.toString(), 
                    "Reject Violation", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    violation.setRejected(true);
                    try {
                        LTOSystem.saveViolations();
                        JOptionPane.showMessageDialog(parentFrame, "Violation rejected successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the panel by removing the old one and creating a new one
                        Component[] components = contentPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JPanel) {
                                // Check if this is the approve violations panel by checking its title
                                JPanel p = (JPanel) comp;
                                if (p.getComponentCount() > 0) {
                                    Component firstComp = p.getComponent(0);
                                    if (firstComp instanceof JPanel) {
                                        JPanel titlePanel = (JPanel) firstComp;
                                        if (titlePanel.getComponentCount() > 0) {
                                            Component titleComp = titlePanel.getComponent(0);
                                            if (titleComp instanceof JLabel) {
                                                String text = ((JLabel) titleComp).getText();
                                                if (text != null && text.contains("Approve/Reject Violations")) {
                                                    contentPanel.remove(p);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        JPanel newPanel = createApproveViolationsPanel(parentFrame, cardLayout, contentPanel);
                        contentPanel.add(newPanel, "APPROVE_VIOLATIONS");
                        cardLayout.show(contentPanel, "APPROVE_VIOLATIONS");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame, "Error saving: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonsPanel.add(rejectBtn);
            
            card.add(infoPanel, BorderLayout.CENTER);
            card.add(buttonsPanel, BorderLayout.EAST);
            
            return card;
        }

        /**
         * Create the Remove Violation panel
         */
        private JPanel createRemoveViolationPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Remove Driver Violation", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            titlePanel.add(titleLabel);
            
            // Input area
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.setBackground(DARK_BLUE);
            inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            inputPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
            
            JTextField nameField = createModernInputField(500, 45);
            JPanel nameInputContainer = createLabeledInputField("Driver's Name or License ID", 
                nameField, 500);
            
            inputPanel.add(nameInputContainer);
            inputPanel.add(Box.createVerticalStrut(20));
            
            JButton searchBtn = new JButton("🔍 Search Violations");
            searchBtn.setPreferredSize(new Dimension(500, 50));
            searchBtn.setMaximumSize(new Dimension(500, 50));
            searchBtn.setBackground(PRIMARY_BLUE);
            searchBtn.setForeground(WHITE);
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            searchBtn.setFocusPainted(false);
            searchBtn.setBorderPainted(false);
            searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    searchBtn.setBackground(PRIMARY_BLUE_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    searchBtn.setBackground(PRIMARY_BLUE);
                }
            });
            inputPanel.add(searchBtn);
            
            // Results area
            JPanel resultsPanel = new JPanel();
            resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
            resultsPanel.setBackground(DARK_BLUE);
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
            resultsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
            
            final JPanel[] resultsContentRef = {new JPanel()};
            resultsContentRef[0].setLayout(new BoxLayout(resultsContentRef[0], BoxLayout.Y_AXIS));
            resultsContentRef[0].setBackground(DARK_BLUE);
            
            JScrollPane resultsScroll = new JScrollPane(resultsContentRef[0]);
            resultsScroll.setBorder(BorderFactory.createEmptyBorder());
            resultsScroll.setBackground(DARK_BLUE);
            resultsScroll.getViewport().setBackground(DARK_BLUE);
            resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            resultsScroll.setPreferredSize(new Dimension(500, 300));
            
            searchBtn.addActionListener(e -> {
                String searchTerm = nameField.getText().trim();
                resultsContentRef[0].removeAll();
                resultsContentRef[0].revalidate();
                resultsContentRef[0].repaint();
                
                if (searchTerm.isEmpty()) {
                    JLabel noTermLabel = createStyledLabel("Please enter a driver's name or license ID", 
                        new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                    noTermLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noTermLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                    resultsContentRef[0].add(noTermLabel);
                } else {
                    // Find ALL matching drivers (not just first one)
                    java.util.List<LTOSystem.User> matchingDrivers = new java.util.ArrayList<>();
                    for (LTOSystem.User user : LTOSystem.users) {
                        if (user.getRole() == LTOSystem.Role.DRIVER) {
                            boolean matches = false;
                            // Check if search term matches license ID exactly
                            if (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(searchTerm)) {
                                matches = true;
                            }
                            // Check if search term matches name (for cases where license ID is provided directly)
                            else if (user.getFullName().equalsIgnoreCase(searchTerm)) {
                                matches = true;
                            }
                            // Check if search term is part of name
                            else if (user.getFullName().toLowerCase().contains(searchTerm.toLowerCase())) {
                                matches = true;
                            }
                            
                            if (matches) {
                                matchingDrivers.add(user);
                            }
                        }
                    }
                    
                    if (matchingDrivers.isEmpty()) {
                        JLabel notFoundLabel = createStyledLabel("❌ No drivers found matching: \"" + searchTerm + "\"", 
                            new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                        notFoundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        notFoundLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                        resultsContentRef[0].add(notFoundLabel);
                    } else if (matchingDrivers.size() == 1) {
                        // Single driver found - directly load violations by license ID
                        LTOSystem.User selectedDriver = matchingDrivers.get(0);
                        loadViolationsForDriver(selectedDriver, resultsContentRef[0], nameField);
                    } else {
                        // Multiple drivers found - show selection list
                        JLabel selectLabel = createStyledLabel("Multiple drivers found. Please select one:", 
                            new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT);
                        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                        resultsContentRef[0].add(selectLabel);
                        
                        for (LTOSystem.User driver : matchingDrivers) {
                            JPanel driverCard = createDriverSelectionCard(driver, resultsContentRef[0], nameField, searchTerm);
                            driverCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                            resultsContentRef[0].add(driverCard);
                            resultsContentRef[0].add(Box.createVerticalStrut(10));
                        }
                    }
                }
                resultsContentRef[0].revalidate();
                resultsContentRef[0].repaint();
                resultsScroll.getViewport().setViewPosition(new java.awt.Point(0, 0));
            });
            
            resultsPanel.add(resultsScroll);
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(DARK_BLUE);
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(inputPanel);
            centerPanel.add(resultsPanel);
            centerPanel.add(buttonPanel);
            centerPanel.add(Box.createVerticalGlue());
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(centerPanel, BorderLayout.CENTER);
            
            return panel;
        }

        /**
         * Load violations for a specific driver using their license ID (not name)
         */
        private void loadViolationsForDriver(LTOSystem.User driver, JPanel resultsContentRef, JTextField nameField) {
            resultsContentRef.removeAll();
            resultsContentRef.revalidate();
            resultsContentRef.repaint();
            
            String driverLicenseId = driver.getLicenseId();
            
            if (driverLicenseId == null || driverLicenseId.trim().isEmpty()) {
                JLabel noLicenseLabel = createStyledLabel("⚠️ This driver has no license ID registered. Cannot fetch violations.", 
                    new Font("Segoe UI", Font.PLAIN, 14), YELLOW_STATUS);
                noLicenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noLicenseLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                resultsContentRef.add(noLicenseLabel);
                return;
            }
            
            // Fetch violations by license ID (NOT by name)
            java.util.List<LTOSystem.Violation> driverViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(driverLicenseId)) {
                    driverViolations.add(v);
                }
            }
            
            if (driverViolations.isEmpty()) {
                JLabel noViolationsLabel = createStyledLabel("✓ No violations found for " + driver.getFullName() + 
                    " (License: " + driverLicenseId + ")", 
                    new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                noViolationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noViolationsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                resultsContentRef.add(noViolationsLabel);
            } else {
                JLabel driverInfoLabel = createStyledLabel("Driver: " + driver.getFullName() + " | License: " + driverLicenseId, 
                    new Font("Segoe UI", Font.BOLD, 14), PRIMARY_BLUE);
                driverInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                driverInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                resultsContentRef.add(driverInfoLabel);
                
                JLabel violationsLabel = createStyledLabel("Found " + driverViolations.size() + " violation(s). Select one to remove:", 
                    new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT);
                violationsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                violationsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                resultsContentRef.add(violationsLabel);
                
                for (LTOSystem.Violation violation : driverViolations) {
                    JPanel violationCard = createViolationCardForRemoval(violation, resultsContentRef, nameField, driver);
                    violationCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultsContentRef.add(violationCard);
                    resultsContentRef.add(Box.createVerticalStrut(10));
                }
            }
            
            resultsContentRef.revalidate();
            resultsContentRef.repaint();
        }

        /**
         * Create a driver selection card when multiple drivers with the same name are found
         */
        private JPanel createDriverSelectionCard(LTOSystem.User driver, JPanel parentPanel, JTextField nameField, String searchTerm) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(CARD_BG);
            
            JLabel nameLabel = createStyledLabel("🚗 " + driver.getFullName(), 
                new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            infoPanel.add(nameLabel);
            
            String licenseId = driver.getLicenseId() != null ? driver.getLicenseId() : "No License ID";
            JLabel licenseLabel = createStyledLabel("License ID: " + licenseId, 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            licenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            infoPanel.add(licenseLabel);
            
            if (!driver.getPlateNumbers().isEmpty()) {
                JLabel plateLabel = createStyledLabel("Plates: " + String.join(", ", driver.getPlateNumbers()), 
                    new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
                infoPanel.add(plateLabel);
            }
            
            JButton selectBtn = new JButton("Select");
            selectBtn.setPreferredSize(new Dimension(100, 40));
            selectBtn.setBackground(PRIMARY_BLUE);
            selectBtn.setForeground(WHITE);
            selectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            selectBtn.setFocusPainted(false);
            selectBtn.setBorderPainted(false);
            selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            selectBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    selectBtn.setBackground(PRIMARY_BLUE_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    selectBtn.setBackground(PRIMARY_BLUE);
                }
            });
            selectBtn.addActionListener(e -> {
                // Get the results panel reference from parent
                JPanel resultsContentRef = parentPanel;
                loadViolationsForDriver(driver, resultsContentRef, nameField);
            });
            
            card.add(infoPanel, BorderLayout.CENTER);
            card.add(selectBtn, BorderLayout.EAST);
            
            return card;
        }

        /**
         * Create a violation card with remove button
         */
        private JPanel createViolationCardForRemoval(LTOSystem.Violation violation, JPanel parentPanel, JTextField nameField, LTOSystem.User driver) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(CARD_BG);
            
            JLabel violationLabel = createStyledLabel("⚠️ " + violation.getType().toString(), 
                new Font("Segoe UI", Font.BOLD, 14), YELLOW_STATUS);
            violationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            infoPanel.add(violationLabel);
            
            JLabel detailsLabel = createStyledLabel("Violator: " + violation.getViolatorName() + 
                " | Fine: ₱" + String.format("%,.2f", violation.getFine()), 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            infoPanel.add(detailsLabel);
            
            if (violation.getLicenseNumber() != null && !violation.getLicenseNumber().isEmpty()) {
                JLabel licenseLabel = createStyledLabel("License: " + violation.getLicenseNumber(), 
                    new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
                licenseLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                infoPanel.add(licenseLabel);
            }
            
            JButton removeBtn = new JButton("🗑️ Remove");
            removeBtn.setPreferredSize(new Dimension(120, 40));
            removeBtn.setBackground(new Color(220, 38, 38));
            removeBtn.setForeground(WHITE);
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            removeBtn.setFocusPainted(false);
            removeBtn.setBorderPainted(false);
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    removeBtn.setBackground(new Color(185, 28, 28));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    removeBtn.setBackground(new Color(220, 38, 38));
                }
            });
            removeBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(parentPanel, 
                    "Are you sure you want to remove this violation?", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    LTOSystem.violations.remove(violation);
                    try { 
                        LTOSystem.saveViolations(); 
                    } catch (Exception ignored) {}
                    JOptionPane.showMessageDialog(parentPanel, "Violation removed successfully!");
                    // Reload violations for the same driver (instead of clearing)
                    if (driver != null) {
                        loadViolationsForDriver(driver, parentPanel, nameField);
                    } else {
                        // Fallback: find driver by license number from violation
                        String violationLicenseId = violation.getLicenseNumber();
                        if (violationLicenseId != null && !violationLicenseId.isEmpty()) {
                            for (LTOSystem.User u : LTOSystem.users) {
                                if (u.getRole() == LTOSystem.Role.DRIVER && 
                                    u.getLicenseId() != null && 
                                    u.getLicenseId().equalsIgnoreCase(violationLicenseId)) {
                                    loadViolationsForDriver(u, parentPanel, nameField);
                                    break;
                                }
                            }
                        } else {
                            // No driver found - just clear
                            parentPanel.removeAll();
                            parentPanel.revalidate();
                            parentPanel.repaint();
                        }
                    }
                }
            });
            
            card.add(infoPanel, BorderLayout.CENTER);
            card.add(removeBtn, BorderLayout.EAST);
            
            return card;
        }

        /**
         * Create the Add Officer panel
         */
        private JPanel createAddOfficerPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Add New Officer", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Form area
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(DARK_BLUE);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
            
            JTextField nameField = createModernInputField(500, 45);
            JPanel nameContainer = createLabeledInputField("Full Name *", nameField, 500);
            formPanel.add(nameContainer);
            formPanel.add(Box.createVerticalStrut(15));
            
            JPasswordField passField = createModernPasswordField(500, 45);
            JPanel passContainer = createLabeledInputField("Password *", passField, 500);
            formPanel.add(passContainer);
            formPanel.add(Box.createVerticalStrut(15));
            
            JTextField emailField = createModernInputField(500, 45);
            JPanel emailContainer = createLabeledInputField("Email *", emailField, 500);
            formPanel.add(emailContainer);
            formPanel.add(Box.createVerticalStrut(30));
            
            JButton submitBtn = new JButton("➕ Add Officer");
            submitBtn.setPreferredSize(new Dimension(500, 50));
            submitBtn.setMaximumSize(new Dimension(500, 50));
            submitBtn.setBackground(PRIMARY_BLUE);
            submitBtn.setForeground(WHITE);
            submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            submitBtn.setFocusPainted(false);
            submitBtn.setBorderPainted(false);
            submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            submitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    submitBtn.setBackground(PRIMARY_BLUE_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    submitBtn.setBackground(PRIMARY_BLUE);
                }
            });
            
            submitBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String pass = new String(passField.getPassword());
                String email = emailField.getText().trim();
                
                // Name validation
                if (!name.matches("^[\\p{L}Ññ ]+$")) {
                    JOptionPane.showMessageDialog(panel, "Name must contain only letters and spaces.");
                    nameField.setText("");
                    nameField.requestFocus();
                    return;
                }
                
                // Password validation
                if (!pass.matches("^[A-Za-z0-9@#$%^&+=]{6,20}$")) {
                    JOptionPane.showMessageDialog(panel, "Password must be 6-20 characters and valid symbols.");
                    passField.setText("");
                    passField.requestFocus();
                    return;
                }
                
                // Email validation
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    JOptionPane.showMessageDialog(panel, "Invalid email format.");
                    emailField.setText("");
                    emailField.requestFocus();
                    return;
                }
                
                // Check if email already exists
                for (LTOSystem.User u : LTOSystem.users) {
                    if (u.getEmail().equalsIgnoreCase(email)) {
                        JOptionPane.showMessageDialog(panel, "Email already exists.");
                        emailField.setText("");
                        emailField.requestFocus();
                        return;
                    }
                }
                
                // Create new officer
                LTOSystem.User newOfficer = new LTOSystem.User(name, pass, email, LTOSystem.Role.OFFICER, null);
                LTOSystem.users.add(newOfficer);
                try { 
                    LTOSystem.saveUsers(); 
                    JOptionPane.showMessageDialog(panel, "Officer added successfully!");
                    nameField.setText("");
                    passField.setText("");
                    emailField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving officer: " + ex.getMessage());
                }
            });
            
            formPanel.add(submitBtn);
            formPanel.add(Box.createVerticalStrut(20));
            
            // Back button
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            formPanel.add(backBtn);
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(DARK_BLUE);
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(formPanel);
            centerPanel.add(Box.createVerticalGlue());
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(centerPanel, BorderLayout.CENTER);
            
            return panel;
        }

        private void showOfficerMenuGUI(LTOSystem.User user) {
            JFrame officerFrame = new JFrame("Officer Menu");
            officerFrame.setSize(1000, 700);
            officerFrame.setLocationRelativeTo(null);
            officerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Main panel with BorderLayout
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            
            // Left sidebar navigation panel (1/3 width)
            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setBackground(DARK_BLUE_LIGHT);
            sidebarPanel.setPreferredSize(new Dimension(300, 0));
            sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // Title in sidebar
            JLabel sidebarTitle = createStyledLabel("Officer Workspace", 
                new Font("Segoe UI", Font.BOLD, 18), WHITE);
            sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
            sidebarPanel.add(sidebarTitle);
            sidebarPanel.add(Box.createVerticalStrut(10));
            
            // Navigation buttons with icons
            // Using Unicode characters for icons: 🚗 (car), ➕ (plus), 🔍 (search), 🗑️ (trash), 🚪 (door), 👤 (person)
            int sidebarBtnWidth = 280;
            JButton addViolationBtn = createSidebarButton("➕", "Add Violation", sidebarBtnWidth, PRIMARY_BLUE);
            JButton searchViolationsBtn = createSidebarButton("🔍", "Search Violations", sidebarBtnWidth, PRIMARY_BLUE);
            JButton deleteViolationBtn = createSidebarButton("🗑️", "Delete Violation", sidebarBtnWidth, PRIMARY_BLUE);
            
            // Logout button with red color
            Color logoutRed = new Color(220, 38, 38); // Red color for logout
            JButton logoutBtn = createSidebarButton("🚪", "Logout", sidebarBtnWidth, logoutRed);
            logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(new Color(Math.min(logoutRed.getRed() + 20, 255), 
                        logoutRed.getGreen(), logoutRed.getBlue()));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(logoutRed);
                }
            });
            
            // Add buttons to sidebar with spacing
            sidebarPanel.add(addViolationBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(searchViolationsBtn);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarPanel.add(deleteViolationBtn);
            sidebarPanel.add(Box.createVerticalGlue()); // Push logout to bottom
            sidebarPanel.add(logoutBtn);
            
            // Right content area with CardLayout for dynamic content switching
            JPanel contentPanel = createDarkPanel();
            CardLayout cardLayout = new CardLayout();
            contentPanel.setLayout(cardLayout);
            
            // Create all view panels
            JPanel welcomePanel = createWelcomePanel();
            JPanel addViolationPanel = createAddViolationPanel(officerFrame, cardLayout, contentPanel);
            
            // Use final arrays to hold panel references for lambda access
            final JPanel[] searchViolationsPanelRef = {createSearchViolationsPanel(officerFrame, cardLayout, contentPanel)};
            final JPanel[] deleteViolationPanelRef = {createDeleteViolationPanel(officerFrame, cardLayout, contentPanel)};
            
            // Add all panels to CardLayout with unique names
            contentPanel.add(welcomePanel, "WELCOME");
            contentPanel.add(addViolationPanel, "ADD_VIOLATION");
            contentPanel.add(searchViolationsPanelRef[0], "SEARCH");
            contentPanel.add(deleteViolationPanelRef[0], "DELETE");
            
            // Show welcome panel by default
            cardLayout.show(contentPanel, "WELCOME");
            
            // Add action listeners to switch cards
            addViolationBtn.addActionListener(e -> cardLayout.show(contentPanel, "ADD_VIOLATION"));
            searchViolationsBtn.addActionListener(e -> {
                // Refresh search panel when switching to it
                contentPanel.remove(searchViolationsPanelRef[0]);
                searchViolationsPanelRef[0] = createSearchViolationsPanel(officerFrame, cardLayout, contentPanel);
                contentPanel.add(searchViolationsPanelRef[0], "SEARCH");
                cardLayout.show(contentPanel, "SEARCH");
            });
            deleteViolationBtn.addActionListener(e -> {
                // Refresh delete panel when switching to it
                contentPanel.remove(deleteViolationPanelRef[0]);
                deleteViolationPanelRef[0] = createDeleteViolationPanel(officerFrame, cardLayout, contentPanel);
                contentPanel.add(deleteViolationPanelRef[0], "DELETE");
                cardLayout.show(contentPanel, "DELETE");
            });
            logoutBtn.addActionListener(e -> navigateToWelcome());
            
            // Add panels to main layout
            mainPanel.add(sidebarPanel, BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            officerFrame.setContentPane(mainPanel);
            navigateToPage(officerFrame);
        }

        /**
         * Create the welcome panel for Officer Workspace
         */
        private JPanel createWelcomePanel() {
            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
            welcomePanel.setBackground(DARK_BLUE);
            welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel welcomeLabel = createStyledLabel("Welcome to Officer Workspace", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            welcomePanel.add(Box.createVerticalGlue());
            welcomePanel.add(welcomeLabel);
            
            JLabel instructionLabel = createStyledLabel("Select an option from the menu to get started", 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            welcomePanel.add(instructionLabel);
            welcomePanel.add(Box.createVerticalGlue());
            
            return welcomePanel;
        }

        /**
         * Create the unified Add Violation panel
         */
        private JPanel createAddViolationPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Add Violation", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Form panel using GridBagLayout to fill available space
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setBackground(DARK_BLUE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            // Full Name field (required)
            JTextField nameField = new JTextField();
            nameField.setBackground(INPUT_BG);
            nameField.setForeground(WHITE);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JLabel nameLabel = createStyledLabel("Full Name *", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(nameLabel, gbc);
            gbc.gridy = 1;
            formPanel.add(nameField, gbc);
            
            // License ID field (optional)
            JTextField licenseField = new JTextField();
            licenseField.setBackground(INPUT_BG);
            licenseField.setForeground(WHITE);
            licenseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            licenseField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JLabel licenseLabel = createStyledLabel("License ID (optional)", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridy = 2;
            formPanel.add(licenseLabel, gbc);
            gbc.gridy = 3;
            formPanel.add(licenseField, gbc);
            
            // Plate Number field (optional)
            JTextField plateField = new JTextField();
            plateField.setBackground(INPUT_BG);
            plateField.setForeground(WHITE);
            plateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            plateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JLabel plateLabel = createStyledLabel("Plate Number (optional)", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridy = 4;
            formPanel.add(plateLabel, gbc);
            gbc.gridy = 5;
            formPanel.add(plateField, gbc);
            
            // Violation type selection
            LTOSystem.ViolationType[] types = LTOSystem.ViolationType.values();
            String[] typeOptions = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                typeOptions[i] = types[i].toString();
            }
            JComboBox<String> violationTypeCombo = new JComboBox<>(typeOptions);
            violationTypeCombo.setBackground(INPUT_BG);
            violationTypeCombo.setForeground(WHITE);
            violationTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            violationTypeCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            violationTypeCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    c.setBackground(isSelected ? PRIMARY_BLUE : INPUT_BG);
                    c.setForeground(WHITE);
                    return c;
                }
            });
            JLabel typeLabel = createStyledLabel("Violation Type *", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridy = 6;
            formPanel.add(typeLabel, gbc);
            gbc.gridy = 7;
            formPanel.add(violationTypeCombo, gbc);
            
            // Submit button
            JButton submitBtn = createModernButton("Add Violation", 200, 45);
            gbc.gridy = 8;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(submitBtn, gbc);
            
            submitBtn.addActionListener(e -> {
                String violatorName = nameField.getText().trim();
                if (violatorName.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Please enter a violator name.");
                    return;
                }
                
                int selectedIndex = violationTypeCombo.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= types.length) {
                    JOptionPane.showMessageDialog(parentFrame, "Please select a violation type.");
                    return;
                }
                
                String licenseId = licenseField.getText().trim();
                if (licenseId.isEmpty()) {
                    licenseId = null;
                }
                
                String plateNumber = plateField.getText().trim().toUpperCase();
                if (plateNumber.isEmpty()) {
                    plateNumber = null;
                }
                
                // Reload violations to get the latest count for ID generation
                LTOSystem.loadViolations();
                
                int baseId = LTOSystem.violations.size();
                java.util.List<LTOSystem.Violation> violationsToAdd = new java.util.ArrayList<>();
                
                // Add the manually selected violation
                LTOSystem.ViolationType selectedType = types[selectedIndex];
                violationsToAdd.add(new LTOSystem.Violation(baseId + 1, violatorName, licenseId, selectedType, selectedType.getBaseFine(), false));
                baseId++;
                
                // Auto-add NO_LICENSE violation if license ID is missing
                if (licenseId == null) {
                    violationsToAdd.add(new LTOSystem.Violation(baseId + 1, violatorName, null, LTOSystem.ViolationType.NO_LICENSE, LTOSystem.ViolationType.NO_LICENSE.getBaseFine(), false));
                    baseId++;
                }
                
                // Auto-add NO_PLATE violation if plate number is missing
                if (plateNumber == null) {
                    violationsToAdd.add(new LTOSystem.Violation(baseId + 1, violatorName, licenseId, LTOSystem.ViolationType.NO_PLATE, LTOSystem.ViolationType.NO_PLATE.getBaseFine(), false));
                }
                
                // Add all violations to the system
                for (LTOSystem.Violation violation : violationsToAdd) {
                    LTOSystem.violations.add(violation);
                }
                
                // Save all violations
                try { 
                    LTOSystem.saveViolations(); 
                    
                    String message = "Violation added successfully!";
                    if (violationsToAdd.size() > 1) {
                        message += "\n" + violationsToAdd.size() + " violations were added (including auto-violations for missing information).";
                    }
                    JOptionPane.showMessageDialog(parentFrame, message);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Error saving violations: " + ex.getMessage());
                    return;
                }
                
                // Clear form
                nameField.setText("");
                licenseField.setText("");
                plateField.setText("");
                violationTypeCombo.setSelectedIndex(0);
            });
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        /**
         * Create the Search Violations panel
         */
        private JPanel createSearchViolationsPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Search Violations", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Search panel using GridBagLayout to fill available space
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());
            searchPanel.setBackground(DARK_BLUE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            // Search field
            JTextField searchField = new JTextField();
            searchField.setBackground(INPUT_BG);
            searchField.setForeground(WHITE);
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JLabel searchLabel = createStyledLabel("Search by violator name, license ID, or plate number", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            searchPanel.add(searchLabel, gbc);
            gbc.gridy = 1;
            searchPanel.add(searchField, gbc);
            
            // Search button
            JButton searchBtn = createModernButton("Search", 150, 40);
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            searchPanel.add(searchBtn, gbc);
            
            // Results area
            JTextArea resultsArea = new JTextArea();
            resultsArea.setEditable(false);
            resultsArea.setBackground(INPUT_BG);
            resultsArea.setForeground(WHITE);
            resultsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            resultsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JScrollPane scrollPane = new JScrollPane(resultsArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            scrollPane.setBackground(INPUT_BG);
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            searchPanel.add(scrollPane, gbc);
            
            // Search action
            searchBtn.addActionListener(e -> {
                String term = searchField.getText().trim();
                if (term.isEmpty()) {
                    resultsArea.setText("Please enter a search term.");
                    return;
                }
                
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (LTOSystem.Violation v : LTOSystem.violations) {
                    if (v.getViolatorName().toLowerCase().contains(term.toLowerCase()) ||
                        (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(term))) {
                        sb.append("Violation #").append(v.getId()).append("\n");
                        sb.append("  Violator: ").append(v.getViolatorName()).append("\n");
                        sb.append("  License: ").append(v.getLicenseNumber() != null ? v.getLicenseNumber() : "N/A").append("\n");
                        sb.append("  Type: ").append(v.getType().getDescription()).append("\n");
                        sb.append("  Fine: ₱").append(String.format("%,.2f", v.getFine())).append("\n");
                        sb.append("  Status: ").append(v.getStatusString()).append("\n");
                        sb.append("  ").append("=".repeat(50)).append("\n\n");
                        count++;
                    }
                }
                
                if (count == 0) {
                    resultsArea.setText("No violations found matching: " + term);
                } else {
                    resultsArea.setText("Found " + count + " violation(s):\n\n" + sb.toString());
                }
            });
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(searchPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        /**
         * Create the Delete Violation panel
         */
        private JPanel createDeleteViolationPanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Delete Violation", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Form panel using GridBagLayout to fill available space
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setBackground(DARK_BLUE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            // License ID field
            JTextField licenseField = new JTextField();
            licenseField.setBackground(INPUT_BG);
            licenseField.setForeground(WHITE);
            licenseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            licenseField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            JLabel licenseLabel = createStyledLabel("Driver's License Number", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(licenseLabel, gbc);
            gbc.gridy = 1;
            formPanel.add(licenseField, gbc);
            
            // Load violations button
            JButton loadBtn = createModernButton("Load Violations", 200, 40);
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(loadBtn, gbc);
            
            // Violations list
            DefaultListModel<String> listModel = new DefaultListModel<>();
            JList<String> violationsList = new JList<>(listModel);
            violationsList.setBackground(INPUT_BG);
            violationsList.setForeground(WHITE);
            violationsList.setFont(new Font("Consolas", Font.PLAIN, 12));
            violationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            violationsList.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            violationsList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    c.setBackground(isSelected ? PRIMARY_BLUE : INPUT_BG);
                    c.setForeground(WHITE);
                    return c;
                }
            });
            JScrollPane listScrollPane = new JScrollPane(violationsList);
            listScrollPane.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(listScrollPane, gbc);
            
            // Delete button
            JButton deleteBtn = createModernButton("Delete Selected Violation", 250, 45);
            gbc.gridy = 4;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            gbc.weighty = 0;
            formPanel.add(deleteBtn, gbc);
            deleteBtn.setEnabled(false);
            
            // Store violations for deletion
            java.util.List<LTOSystem.Violation> loadedViolations = new java.util.ArrayList<>();
            
            // Load violations action
            loadBtn.addActionListener(e -> {
                String licenseId = licenseField.getText().trim();
                if (licenseId.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Please enter a license number.");
                    return;
                }
                
                loadedViolations.clear();
                listModel.clear();
                
                for (LTOSystem.Violation v : LTOSystem.violations) {
                    if (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(licenseId)) {
                        loadedViolations.add(v);
                        String display = String.format("Violation #%d - %s (₱%,.2f) - %s", 
                            v.getId(), v.getType().getDescription(), v.getFine(),
                            v.isPaid() ? "PAID" : "UNPAID");
                        listModel.addElement(display);
                    }
                }
                
                if (loadedViolations.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "No violations found for license number: " + licenseId);
                    deleteBtn.setEnabled(false);
                } else {
                    deleteBtn.setEnabled(true);
                }
            });
            
            // Enable delete button when selection changes
            violationsList.addListSelectionListener(e -> {
                deleteBtn.setEnabled(!violationsList.isSelectionEmpty() && !loadedViolations.isEmpty());
            });
            
            // Delete action
            deleteBtn.addActionListener(e -> {
                int selectedIndex = violationsList.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= loadedViolations.size()) {
                    JOptionPane.showMessageDialog(parentFrame, "Please select a violation to delete.");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(parentFrame, 
                    "Are you sure you want to delete this violation?", 
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    LTOSystem.Violation violationToDelete = loadedViolations.get(selectedIndex);
                    LTOSystem.violations.remove(violationToDelete);
                    try { 
                        LTOSystem.saveViolations(); 
                    } catch (Exception ignored) {}
                    
                    JOptionPane.showMessageDialog(parentFrame, "Violation deleted successfully!");
                    
                    // Refresh the list
                    loadBtn.doClick();
                }
            });
            
            // Back button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
            buttonPanel.setBackground(DARK_BLUE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton backBtn = createModernButton("← Back", 120, 40);
            backBtn.setBackground(DARK_BLUE_LIGHT);
            backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
            backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                        DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    backBtn.setBackground(DARK_BLUE_LIGHT);
                }
            });
            backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
            buttonPanel.add(backBtn);
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }

        private void showDriverMenuGUI(LTOSystem.User user) {
            JFrame driverFrame = new JFrame("Driver Dashboard - LTO System");
            driverFrame.setSize(900, 700);
            driverFrame.setLocationRelativeTo(null);
            driverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = createDarkPanel();
            mainPanel.setLayout(new BorderLayout());
            
            // Top header
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBackground(DARK_BLUE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            
            JLabel titleLabel = createStyledLabel("Driver Dashboard", 
                new Font("Segoe UI", Font.BOLD, 28), WHITE);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            JButton logoutBtn = createModernButton("Logout", 100, 35);
            logoutBtn.addActionListener(e -> navigateToWelcome());
            headerPanel.add(logoutBtn, BorderLayout.EAST);
            
            // Content area with CardLayout
            JPanel contentPanel = createDarkPanel();
            CardLayout cardLayout = new CardLayout();
            contentPanel.setLayout(cardLayout);
            
            // Create panels
            JPanel welcomePanel = createDriverWelcomePanel(user);
            JPanel payFinePanel = createPayFinePanel(driverFrame, cardLayout, contentPanel, user);
            
            // Add panels to CardLayout
            contentPanel.add(welcomePanel, "WELCOME");
            contentPanel.add(payFinePanel, "PAY_FINE");
            
            // Show welcome panel by default
            cardLayout.show(contentPanel, "WELCOME");
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            driverFrame.setContentPane(mainPanel);
            navigateToPage(driverFrame);
        }
        
        private JPanel createDriverWelcomePanel(LTOSystem.User user) {
            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
            welcomePanel.setBackground(DARK_BLUE);
            welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Driver info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
            infoPanel.setBackground(DARK_BLUE);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            
            JLabel licenseLabel = createStyledLabel("License: " + (user.getLicenseId() != null ? user.getLicenseId() : "N/A"), 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            infoPanel.add(licenseLabel);
            
            JLabel nameLabel = createStyledLabel("Name: " + user.getFullName(), 
                new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
            infoPanel.add(nameLabel);
            welcomePanel.add(infoPanel);
            
            // Main content area with scroll
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(DARK_BLUE);
            
            // Recent Violations section
            JLabel violationsTitle = createStyledLabel("Recent Violations", 
                new Font("Segoe UI", Font.BOLD, 18), WHITE);
            violationsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            contentPanel.add(violationsTitle);
            
            // Violations grid panel
            JPanel violationsGrid = new JPanel();
            violationsGrid.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
            violationsGrid.setBackground(DARK_BLUE);
            
            // Load violations for this user
            java.util.List<LTOSystem.Violation> userViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().equalsIgnoreCase(user.getFullName())) {
                    userViolations.add(v);
                }
            }
            
            if (userViolations.isEmpty()) {
                JLabel noViolationsLabel = createStyledLabel("No violations found.", 
                    new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                noViolationsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                violationsGrid.add(noViolationsLabel);
            } else {
                // Show up to 6 violations in a grid
                int cardWidth = 280;
                for (int i = 0; i < Math.min(userViolations.size(), 6); i++) {
                    violationsGrid.add(createViolationCard(userViolations.get(i), cardWidth));
                }
            }
            
            contentPanel.add(violationsGrid);
            contentPanel.add(Box.createVerticalStrut(30));
            
            // Total Balance section
            double totalBalance = userViolations.stream()
                .filter(v -> !v.isPaid())
                .mapToDouble(v -> v.getFine())
                .sum();
            
            JPanel balancePanel = new JPanel();
            balancePanel.setLayout(new BorderLayout(20, 15));
            balancePanel.setBackground(PRIMARY_BLUE);
            balancePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            balancePanel.setPreferredSize(new Dimension(800, 100));
            balancePanel.setMaximumSize(new Dimension(800, 100));
            
            JLabel balanceLabel = createStyledLabel("Total Balance: ₱" + String.format("%,.2f", totalBalance), 
                new Font("Segoe UI", Font.BOLD, 20), WHITE);
            balancePanel.add(balanceLabel, BorderLayout.NORTH);
            
            JLabel paymentNote = createStyledLabel("Payment required at Admin office.", 
                new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
            balancePanel.add(paymentNote, BorderLayout.CENTER);
            
            // Action buttons
            JPanel actionPanel = new JPanel();
            actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
            actionPanel.setBackground(PRIMARY_BLUE);
            actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            
            JButton payFineBtn = createModernButton("Pay Fine", 150, 40);
            payFineBtn.addActionListener(e -> {
                CardLayout cl = (CardLayout) welcomePanel.getParent().getLayout();
                cl.show(welcomePanel.getParent(), "PAY_FINE");
            });
            actionPanel.add(payFineBtn);
            
            JButton managePlatesBtn = createModernButton("Manage Plates", 150, 40);
            managePlatesBtn.addActionListener(e -> {
                // Get parent frame from welcome panel
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(welcomePanel);
                manageVehiclePlatesGUI(parentFrame, user);
            });
            actionPanel.add(managePlatesBtn);
            
            balancePanel.add(actionPanel, BorderLayout.SOUTH);
            contentPanel.add(balancePanel);
            
            // Scroll pane for content
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBackground(DARK_BLUE);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(DARK_BLUE);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            welcomePanel.add(scrollPane);
            
            return welcomePanel;
        }
        
        private JPanel createPayFinePanel(JFrame parentFrame, CardLayout cardLayout, JPanel contentPanel, LTOSystem.User user) {
            JPanel panel = createDarkPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBackground(DARK_BLUE);
            titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel titleLabel = createStyledLabel("Pay Fine", 
                new Font("Segoe UI", Font.BOLD, 24), TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            titlePanel.add(titleLabel);
            
            // Get APPROVED and unpaid violations only
            java.util.List<LTOSystem.Violation> payableViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().equalsIgnoreCase(user.getFullName()) && 
                    v.isApproved() && !v.isPaid()) {
                    payableViolations.add(v);
                }
            }
            
            // Content panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(DARK_BLUE);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            if (payableViolations.isEmpty()) {
                // Check if there are any unpaid violations at all
                java.util.List<LTOSystem.Violation> allUnpaid = new java.util.ArrayList<>();
                for (LTOSystem.Violation v : LTOSystem.violations) {
                    if (v.getViolatorName().equalsIgnoreCase(user.getFullName()) && !v.isPaid()) {
                        allUnpaid.add(v);
                    }
                }
                
                if (allUnpaid.isEmpty()) {
                    JLabel noViolationsLabel = createStyledLabel("You have no unpaid violations.", 
                        new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                    noViolationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noViolationsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                    formPanel.add(noViolationsLabel);
                } else {
                    // Show message about pending/rejected violations
                    StringBuilder message = new StringBuilder();
                    message.append("<html><div style='width:500px;'>");
                    message.append("You have no approved violations available for payment.<br><br>");
                    
                    boolean hasPending = false;
                    boolean hasRejected = false;
                    for (LTOSystem.Violation v : allUnpaid) {
                        if (!v.isApproved() && !v.isRejected()) {
                            hasPending = true;
                        } else if (v.isRejected()) {
                            hasRejected = true;
                        }
                    }
                    
                    if (hasPending) {
                        message.append("Some violations are still <b>PENDING</b> admin approval.<br>");
                    }
                    if (hasRejected) {
                        message.append("Some violations have been <b>REJECTED</b>.<br>");
                    }
                    message.append("<br>Only <b>APPROVED</b> violations can be paid.</div></html>");
                    
                    JLabel statusLabel = createStyledLabel(message.toString(), 
                        new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                    statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                    formPanel.add(statusLabel);
                }
            } else {
                JLabel selectLabel = createStyledLabel("Select a violation to pay:", 
                    new Font("Segoe UI", Font.PLAIN, 14), TEXT_LIGHT);
                selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                formPanel.add(selectLabel);
                
                // Create violation list with radio buttons in a scrollable panel
                JPanel violationsListPanel = new JPanel();
                violationsListPanel.setLayout(new BoxLayout(violationsListPanel, BoxLayout.Y_AXIS));
                violationsListPanel.setBackground(DARK_BLUE);
                violationsListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                ButtonGroup violationGroup = new ButtonGroup();
                java.util.List<JRadioButton> radioButtons = new java.util.ArrayList<>();
                
                for (LTOSystem.Violation v : payableViolations) {
                    // Create a panel for each violation with radio button and wrapped text
                    JPanel violationItemPanel = new JPanel(new BorderLayout(10, 5));
                    violationItemPanel.setBackground(DARK_BLUE);
                    violationItemPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
                    violationItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    violationItemPanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
                    
                    JRadioButton radioBtn = new JRadioButton();
                    radioBtn.setBackground(DARK_BLUE);
                    radioBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                    violationGroup.add(radioBtn);
                    radioButtons.add(radioBtn);
                    
                    // Create label with wrapped text
                    String violationText = v.toString();
                    JLabel violationLabel = createStyledLabel("<html><div style='width:600px;'>" + violationText.replace("\n", "<br>") + "</div></html>", 
                        new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
                    violationLabel.setVerticalAlignment(JLabel.TOP);
                    
                    violationItemPanel.add(radioBtn, BorderLayout.WEST);
                    violationItemPanel.add(violationLabel, BorderLayout.CENTER);
                    
                    violationsListPanel.add(violationItemPanel);
                }
                
                // Select first violation by default
                if (!radioButtons.isEmpty()) {
                    radioButtons.get(0).setSelected(true);
                }
                
                // Wrap in scroll pane
                JScrollPane violationsScrollPane = new JScrollPane(violationsListPanel);
                violationsScrollPane.setBackground(DARK_BLUE);
                violationsScrollPane.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
                violationsScrollPane.getViewport().setBackground(DARK_BLUE);
                violationsScrollPane.setPreferredSize(new Dimension(700, 300));
                violationsScrollPane.setMaximumSize(new Dimension(700, 300));
                violationsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
                violationsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                violationsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
                formPanel.add(violationsScrollPane);
                
                // Buttons panel
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
                buttonsPanel.setBackground(DARK_BLUE);
                buttonsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
                
                JButton payBtn = createModernButton("Pay Selected Fine", 180, 45);
                payBtn.addActionListener(e -> {
                    int selectedIndex = -1;
                    for (int i = 0; i < radioButtons.size(); i++) {
                        if (radioButtons.get(i).isSelected()) {
                            selectedIndex = i;
                            break;
                        }
                    }
                    
                    if (selectedIndex >= 0 && selectedIndex < payableViolations.size()) {
                        LTOSystem.Violation violationToPay = payableViolations.get(selectedIndex);
                        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
                            "Pay fine for this violation?\n" + violationToPay.toString(), 
                            "Confirm Payment", JOptionPane.OK_CANCEL_OPTION);
                        if (confirm == JOptionPane.OK_OPTION) {
                            try {
                                violationToPay.processPayment();
                                LTOSystem.saveViolations();
                                JOptionPane.showMessageDialog(parentFrame, "Fine paid successfully!", 
                                    "Pay Fine", JOptionPane.INFORMATION_MESSAGE);
                                // Refresh the panel
                                contentPanel.remove(panel);
                                JPanel newPayFinePanel = createPayFinePanel(parentFrame, cardLayout, contentPanel, user);
                                contentPanel.add(newPayFinePanel, "PAY_FINE");
                                cardLayout.show(contentPanel, "PAY_FINE");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage(), 
                                    "Pay Fine", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
                buttonsPanel.add(payBtn);
                
                JButton backBtn = createModernButton("Back", 120, 45);
                backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
                buttonsPanel.add(backBtn);
                
                formPanel.add(buttonsPanel);
            }
            
            // Always add back button at the bottom (even if no violations)
            if (payableViolations.isEmpty()) {
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
                buttonPanel.setBackground(DARK_BLUE);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                
                JButton backBtn = createModernButton("← Back", 120, 40);
                backBtn.setBackground(DARK_BLUE_LIGHT);
                backBtn.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
                backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        backBtn.setBackground(new Color(DARK_BLUE_LIGHT.getRed() + 20, 
                            DARK_BLUE_LIGHT.getGreen() + 20, DARK_BLUE_LIGHT.getBlue() + 20));
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        backBtn.setBackground(DARK_BLUE_LIGHT);
                    }
                });
                backBtn.addActionListener(e -> cardLayout.show(contentPanel, "WELCOME"));
                buttonPanel.add(backBtn);
                
                formPanel.add(buttonPanel);
            }
            
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(formPanel, BorderLayout.CENTER);
            
            return panel;
        }

        // Pay Fine GUI for drivers
        private void payFineGUI(JFrame parent, LTOSystem.User user) {
            // Only show APPROVED and unpaid violations
            java.util.List<LTOSystem.Violation> payableViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().equalsIgnoreCase(user.getFullName()) && 
                    v.isApproved() && !v.isPaid()) {
                    payableViolations.add(v);
                }
            }
            
            if (payableViolations.isEmpty()) {
                // Check if there are any violations at all
                java.util.List<LTOSystem.Violation> allUserViolations = new java.util.ArrayList<>();
                for (LTOSystem.Violation v : LTOSystem.violations) {
                    if (v.getViolatorName().equalsIgnoreCase(user.getFullName()) && !v.isPaid()) {
                        allUserViolations.add(v);
                    }
                }
                
                if (allUserViolations.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "You have no unpaid violations.", "Pay Fine", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Check status of violations
                    boolean hasPending = false;
                    boolean hasRejected = false;
                    for (LTOSystem.Violation v : allUserViolations) {
                        if (!v.isApproved() && !v.isRejected()) {
                            hasPending = true;
                        } else if (v.isRejected()) {
                            hasRejected = true;
                        }
                    }
                    
                    StringBuilder message = new StringBuilder("You have no approved violations available for payment.\n\n");
                    if (hasPending) {
                        message.append("Some violations are still pending admin approval.\n");
                    }
                    if (hasRejected) {
                        message.append("Some violations have been rejected.\n");
                    }
                    message.append("\nOnly approved violations can be paid.");
                    
                    JOptionPane.showMessageDialog(parent, message.toString(), "Pay Fine", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
            
            String[] vOptions = new String[payableViolations.size()];
            for (int i = 0; i < payableViolations.size(); i++) {
                vOptions[i] = payableViolations.get(i).toString();
            }
            int vChoice = JOptionPane.showOptionDialog(parent, "Select an approved violation to pay:", "Pay Fine", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, vOptions, vOptions[0]);
            if (vChoice >= 0 && vChoice < payableViolations.size()) {
                LTOSystem.Violation violationToPay = payableViolations.get(vChoice);
                int confirm = JOptionPane.showConfirmDialog(parent, "Pay fine for this violation?\n" + violationToPay.toString(), "Confirm Payment", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    try {
                        violationToPay.processPayment();
                        LTOSystem.saveViolations();
                        JOptionPane.showMessageDialog(parent, "Fine paid successfully!", "Pay Fine", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(), "Pay Fine", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        // --- GUI versions of menu actions, now take JFrame as parent ---
        private void displayAllUsersGUI(JFrame parent) {
            StringBuilder sb = new StringBuilder();
            for (LTOSystem.User user : LTOSystem.users) {
                sb.append(user.getRole()).append(": ").append(user.getFullName()).append(" | Email: ").append(user.getEmail());
                if (user.getRole() == LTOSystem.Role.DRIVER) {
                    sb.append(" | License: ").append(user.getLicenseId());
                    sb.append(" | Plates: ").append(String.join(", ", user.getPlateNumbers()));
                }
                sb.append("\n");
            }
            JOptionPane.showMessageDialog(parent, sb.length() > 0 ? sb.toString() : "No users found.", "All Users", JOptionPane.INFORMATION_MESSAGE);
        }
        private void searchUsersGUI(JFrame parent) {
            String term = JOptionPane.showInputDialog(parent, "Enter name, license ID, or plate number:");
            if (term == null || term.isEmpty()) return;
            StringBuilder sb = new StringBuilder();
            for (LTOSystem.User user : LTOSystem.users) {
                if (user.getFullName().toLowerCase().contains(term.toLowerCase()) ||
                    (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(term)) ||
                    user.getPlateNumbers().contains(term.toUpperCase())) {
                    sb.append(user.getRole()).append(": ").append(user.getFullName()).append(" | Email: ").append(user.getEmail());
                    if (user.getRole() == LTOSystem.Role.DRIVER) {
                        sb.append(" | License: ").append(user.getLicenseId());
                        sb.append(" | Plates: ").append(String.join(", ", user.getPlateNumbers()));
                    }
                    sb.append("\n");
                }
            }
            JOptionPane.showMessageDialog(parent, sb.length() > 0 ? sb.toString() : "No users found.", "Search Users", JOptionPane.INFORMATION_MESSAGE);
        }
        private void removeDriverViolationGUI(JFrame parent) {
            String name = JOptionPane.showInputDialog(parent, "Enter driver's name or license ID:");
            if (name == null || name.isEmpty()) return;
            LTOSystem.User driver = null;
            for (LTOSystem.User user : LTOSystem.users) {
                if (user.getRole() == LTOSystem.Role.DRIVER && (user.getFullName().equalsIgnoreCase(name) || (user.getLicenseId() != null && user.getLicenseId().equalsIgnoreCase(name)))) {
                    driver = user;
                    break;
                }
            }
            if (driver == null) {
                JOptionPane.showMessageDialog(parent, "Driver not found.");
                return;
            }
            java.util.List<LTOSystem.Violation> driverViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().equalsIgnoreCase(driver.getFullName())) {
                    driverViolations.add(v);
                }
            }
            if (driverViolations.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "No violations found for this driver.");
                return;
            }
            String[] vOptions = new String[driverViolations.size()];
            for (int i = 0; i < driverViolations.size(); i++) {
                vOptions[i] = driverViolations.get(i).toString();
            }
            int vChoice = JOptionPane.showOptionDialog(parent, "Select violation to remove:", "Remove Violation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, vOptions, vOptions[0]);
            if (vChoice >= 0 && vChoice < driverViolations.size()) {
                LTOSystem.Violation violationToRemove = driverViolations.get(vChoice);
                LTOSystem.violations.remove(violationToRemove);
                try { LTOSystem.saveViolations(); } catch (Exception ignored) {}
                JOptionPane.showMessageDialog(parent, "Violation removed successfully!");
            }
        }
        private void addNewOfficerGUI(JFrame parent) {
            JTextField nameField = new JTextField();
            JPasswordField passField = new JPasswordField();
            JTextField emailField = new JTextField();
            Object[] fields = {
                "Full Name:", nameField,
                "Password:", passField,
                "Email:", emailField
            };
            int result = JOptionPane.showConfirmDialog(parent, fields, "Add New Officer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String pass = new String(passField.getPassword());
                String email = emailField.getText().trim();
                // Name validation: only letters and spaces
                if (!name.matches("^[\\p{L}Ññ ]+$")) {
                    JOptionPane.showMessageDialog(parent, "Name must contain only letters and spaces.");
                    nameField.setText("");
                    nameField.requestFocus();
                    return;
                }
                // Password validation
                if (!pass.matches("^[A-Za-z0-9@#$%^&+=]{6,20}$")) {
                    JOptionPane.showMessageDialog(parent, "Password must be 6-20 characters and valid symbols.");
                    passField.setText("");
                    passField.requestFocus();
                    return;
                }
                // Email validation
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    JOptionPane.showMessageDialog(parent, "Invalid email format.");
                    emailField.setText("");
                    emailField.requestFocus();
                    return;
                }
                LTOSystem.User newOfficer = new LTOSystem.User(name, pass, email, LTOSystem.Role.OFFICER, null);
                LTOSystem.users.add(newOfficer);
                try { LTOSystem.saveUsers(); } catch (Exception ignored) {}
                JOptionPane.showMessageDialog(parent, "New officer added successfully!");
            }
        }
        private void addViolationByLicenseGUI(JFrame parent) {
            String licenseId = JOptionPane.showInputDialog(parent, "Enter driver's license number:");
            if (licenseId == null || licenseId.isEmpty()) return;
            LTOSystem.DriversLicense license = LTOSystem.findLicense(licenseId);
            String violatorName = (license != null) ? license.getFullName() : licenseId;
            LTOSystem.ViolationType[] types = LTOSystem.ViolationType.values();
            String[] vOptions = new String[types.length];
            for (int i = 0; i < types.length; i++) vOptions[i] = types[i].toString();
            int vChoice = JOptionPane.showOptionDialog(parent, "Select violation type:", "Violation Type", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, vOptions, vOptions[0]);
            if (vChoice >= 0 && vChoice < types.length) {
                LTOSystem.ViolationType type = types[vChoice];
                int newId = LTOSystem.violations.size() + 1;
                LTOSystem.Violation violation = new LTOSystem.Violation(newId, violatorName, licenseId, type, type.getBaseFine(), false);
                LTOSystem.violations.add(violation);
                try { LTOSystem.saveViolations(); } catch (Exception ignored) {}
                JOptionPane.showMessageDialog(parent, "Violation added successfully!");
            }
        }
        private void addViolationByPlateGUI(JFrame parent) {
            String plate = JOptionPane.showInputDialog(parent, "Enter plate number:");
            if (plate == null || plate.isEmpty()) return;
            LTOSystem.User violator = null;
            for (LTOSystem.User user : LTOSystem.users) {
                if (user.getPlateNumbers().contains(plate.toUpperCase())) {
                    violator = user;
                    break;
                }
            }
            String violatorName = (violator != null) ? violator.getFullName() : plate;
            String licenseId = (violator != null) ? violator.getLicenseId() : null;
            LTOSystem.ViolationType[] types = LTOSystem.ViolationType.values();
            String[] vOptions = new String[types.length];
            for (int i = 0; i < types.length; i++) vOptions[i] = types[i].toString();
            int vChoice = JOptionPane.showOptionDialog(parent, "Select violation type:", "Violation Type", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, vOptions, vOptions[0]);
            if (vChoice >= 0 && vChoice < types.length) {
                LTOSystem.ViolationType type = types[vChoice];
                int newId = LTOSystem.violations.size() + 1;
                LTOSystem.Violation violation = new LTOSystem.Violation(newId, violatorName, licenseId, type, type.getBaseFine(), false);
                LTOSystem.violations.add(violation);
                try { LTOSystem.saveViolations(); } catch (Exception ignored) {}
                JOptionPane.showMessageDialog(parent, "Violation added successfully!");
            }
        }
        private void searchViolationsGUI(JFrame parent) {
            String term = JOptionPane.showInputDialog(parent, "Enter violator name, license ID, or plate number:");
            if (term == null || term.isEmpty()) return;
            StringBuilder sb = new StringBuilder();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().toLowerCase().contains(term.toLowerCase()) ||
                    (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(term))) {
                    sb.append(v.toString()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(parent, sb.length() > 0 ? sb.toString() : "No violations found.", "Search Violations", JOptionPane.INFORMATION_MESSAGE);
        }
        private void deleteViolationGUI(JFrame parent) {
            String licenseId = JOptionPane.showInputDialog(parent, "Enter driver's license number:");
            if (licenseId == null || licenseId.isEmpty()) return;
            java.util.List<LTOSystem.Violation> driverViolations = new java.util.ArrayList<>();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getLicenseNumber() != null && v.getLicenseNumber().equalsIgnoreCase(licenseId)) {
                    driverViolations.add(v);
                }
            }
            if (driverViolations.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "No violations found for license number: " + licenseId);
                return;
            }
            String[] vOptions = new String[driverViolations.size()];
            for (int i = 0; i < driverViolations.size(); i++) {
                vOptions[i] = driverViolations.get(i).toString();
            }
            int vChoice = JOptionPane.showOptionDialog(parent, "Select violation to delete:", "Delete Violation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, vOptions, vOptions[0]);
            if (vChoice >= 0 && vChoice < driverViolations.size()) {
                LTOSystem.Violation violationToDelete = driverViolations.get(vChoice);
                LTOSystem.violations.remove(violationToDelete);
                try { LTOSystem.saveViolations(); } catch (Exception ignored) {}
                JOptionPane.showMessageDialog(parent, "Violation deleted successfully!");
            }
        }
        private void displayUserViolationsGUI(JFrame parent, LTOSystem.User user) {
            StringBuilder sb = new StringBuilder();
            for (LTOSystem.Violation v : LTOSystem.violations) {
                if (v.getViolatorName().equalsIgnoreCase(user.getFullName())) {
                    sb.append(v.toString()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(parent, sb.length() > 0 ? sb.toString() : "No violations found.", "My Violations", JOptionPane.INFORMATION_MESSAGE);
        }
        private void manageVehiclePlatesGUI(JFrame parent, LTOSystem.User user) {
            String[] options = {"Add Plate", "Remove Plate", "Back"};
            while (true) {
                int choice = JOptionPane.showOptionDialog(parent, "Manage Vehicle Plates\nCurrent: " + String.join(", ", user.getPlateNumbers()), "Vehicle Plates", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    String[] types = {"Car", "Motorcycle"};
                    int typeChoice = JOptionPane.showOptionDialog(parent, "Select vehicle type:", "Plate Type", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
                    if (typeChoice == 0) { // Car
                        JTextField plateField = new JTextField("ABC 1234");
                        plateField.addKeyListener(new java.awt.event.KeyAdapter() {
                            @Override
                            public void keyReleased(java.awt.event.KeyEvent e) {
                                String text = plateField.getText().replaceAll("[^A-Z0-9]", "").toUpperCase();
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < text.length() && i < 7; i++) {
                                    sb.append(text.charAt(i));
                                    if (i == 2) sb.append(' ');
                                }
                                plateField.setText(sb.toString());
                            }
                        });
                        int result = JOptionPane.showConfirmDialog(parent, plateField, "Enter car plate number (LLL ####)", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            String newPlate = plateField.getText().trim().toUpperCase();
                            if (!newPlate.matches("^[A-Z]{3} [0-9]{4}$")) {
                                JOptionPane.showMessageDialog(parent, "Invalid car plate format. Use LLL #### (e.g., ABC 1234)");
                                continue;
                            }
                            if (user.getPlateNumbers().contains(newPlate)) {
                                JOptionPane.showMessageDialog(parent, "This plate is already registered to you.");
                                continue;
                            }
                            user.addPlateNumber(newPlate);
                            try { LTOSystem.saveUsers(); } catch (Exception ignored) {}
                            JOptionPane.showMessageDialog(parent, "Plate added!");
                        }
                    } else if (typeChoice == 1) { // Motorcycle
                        JTextField plateField = new JTextField("A 123 BC");
                        plateField.addKeyListener(new java.awt.event.KeyAdapter() {
                            @Override
                            public void keyReleased(java.awt.event.KeyEvent e) {
                                String text = plateField.getText().replaceAll("[^A-Z0-9]", "").toUpperCase();
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < text.length() && i < 6; i++) {
                                    sb.append(text.charAt(i));
                                    if (i == 0) sb.append(' ');
                                    if (i == 3) sb.append(' ');
                                }
                                // Limit to L ### LL (8 chars with spaces)
                                String formatted = sb.toString();
                                if (formatted.length() > 8) formatted = formatted.substring(0, 8);
                                plateField.setText(formatted);
                            }
                        });
                        int result = JOptionPane.showConfirmDialog(parent, plateField, "Enter motorcycle plate number (L ### LL)", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            String newPlate = plateField.getText().trim().toUpperCase();
                            // Strictly enforce L ### LL (e.g., A 123 BC)
                            if (!newPlate.matches("^[A-Z] [0-9]{3} [A-Z]{2}$")) {
                                JOptionPane.showMessageDialog(parent, "Invalid motorcycle plate format. Use L ### LL (e.g., A 123 BC)");
                                continue;
                            }
                            if (user.getPlateNumbers().contains(newPlate)) {
                                JOptionPane.showMessageDialog(parent, "This plate is already registered to you.");
                                continue;
                            }
                            user.addPlateNumber(newPlate);
                            try { LTOSystem.saveUsers(); } catch (Exception ignored) {}
                            JOptionPane.showMessageDialog(parent, "Plate added!");
                        }
                    }
                } else if (choice == 1) {
                    String plateToRemove = JOptionPane.showInputDialog(parent, "Enter plate number to remove:");
                    if (plateToRemove != null && !plateToRemove.isEmpty() && user.getPlateNumbers().remove(plateToRemove.toUpperCase())) {
                        try { LTOSystem.saveUsers(); } catch (Exception ignored) {}
                        JOptionPane.showMessageDialog(parent, "Plate removed!");
                    } else {
                        JOptionPane.showMessageDialog(parent, "Plate not found.");
                    }
                } else {
                    break;
                }
            }
        }
    }
}
