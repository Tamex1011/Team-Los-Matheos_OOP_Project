package utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * 
 * This demonstrates:
 * - Utility classes: Static methods for common operations
 * - Encapsulation: Validation logic is centralized
 */
public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9@#$%^&+=]{6,20}$");
    private static final Pattern LICENSE_ID_PATTERN = Pattern.compile("^[A-Z]\\d{2}-\\d{2}-\\d{6}$");
    private static final Pattern AUTOMOBILE_PLATE_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{4}$");
    private static final Pattern MOTORCYCLE_PLATE_PATTERN = Pattern.compile("^[A-Z]{2}-\\d{5}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}Ññ]+(\\s[\\p{L}Ññ]+)*$", Pattern.UNICODE_CHARACTER_CLASS);

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        if (!EMAIL_PATTERN.matcher(email).matches()) return false;
        String[] parts = email.split("@");
        return parts.length == 2 && parts[0].matches(".*[a-zA-Z].*");
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidLicenseId(String licenseId) {
        return licenseId != null && LICENSE_ID_PATTERN.matcher(licenseId.toUpperCase()).matches();
    }

    public static boolean isValidPlateNumber(String plateNumber, boolean isAutomobile) {
        if (plateNumber == null) return false;
        Pattern pattern = isAutomobile ? AUTOMOBILE_PLATE_PATTERN : MOTORCYCLE_PLATE_PATTERN;
        return pattern.matcher(plateNumber.toUpperCase()).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
}

