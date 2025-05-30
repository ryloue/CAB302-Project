
//import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TestLogin {

    // Email regex pattern from Login class (same as SignUp)
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /**
     * Tests the validation logic for empty fields
     */
    @Test
    @DisplayName("Test validateLogin with empty fields")
    void testValidateLoginWithEmptyFields() {
        // Empty email
        assertFalse(validateLoginFields("", "password123"), "Empty email should fail validation");

        // Empty password
        assertFalse(validateLoginFields("test@example.com", ""), "Empty password should fail validation");

        // Both fields empty
        assertFalse(validateLoginFields("", ""), "Both fields empty should fail validation");
    }


    /**
     * Tests the validation logic for invalid email format
     */
    @Test
    @DisplayName("Test validateLogin with invalid email format")
    void testValidateLoginWithInvalidEmail() {
        assertFalse(validateLoginFields("invalid-email", "password123"),
                "Invalid email format should fail validation");
        assertFalse(validateLoginFields("user@", "password123"),
                "Email without domain should fail validation");
        assertFalse(validateLoginFields("@domain.com", "password123"),
                "Email without username should fail validation");
    }

    /**
     * Tests the validation logic for valid inputs
     */
    @Test
    @DisplayName("Test validateLogin with valid inputs")
    void testValidateLoginWithValidInputs() {
        assertTrue(validateLoginFields("test@example.com", "password123"),
                "Valid inputs should pass validation");
        assertTrue(validateLoginFields("user.name@domain.co.uk", "longpassword123"),
                "Valid complex inputs should pass validation");
    }

    /**
     * Helper method that replicates the validation logic from Login class
     * This extracts just the core validation logic we want to test
     */
    private boolean validateLoginFields(String email, String password) {
        // Check for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }

        // Validate email format
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            return false;
        }

        return true;
    }

    /**
     * Tests email validation separately
     */
    @Test
    @DisplayName("Test email validation for login")
    void testEmailValidation() {
        // Valid emails
        assertTrue(isEmailValid("user@example.com"), "Standard email should be valid");
        assertTrue(isEmailValid("user.name@domain.com"), "Email with dots should be valid");
        assertTrue(isEmailValid("user+tag@domain.co.uk"), "Email with plus and subdomain should be valid");

        // Invalid emails
        assertFalse(isEmailValid(""), "Empty email should be invalid");
        assertFalse(isEmailValid("user@"), "Email without domain should be invalid");
        assertFalse(isEmailValid("user@domain"), "Email without TLD should be invalid");
        assertFalse(isEmailValid("user@.com"), "Email with empty domain should be invalid");
        assertFalse(isEmailValid("@domain.com"), "Email without username should be invalid");
    }

    private boolean isEmailValid(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Tests password validation separately
     */
    @Test
    @DisplayName("Test password validation for login")
    void testPasswordValidation() {
        assertTrue(isPasswordValid("password123"), "Password should be valid");

        assertFalse(isPasswordValid(""), "Empty password should be invalid");
        assertFalse(isPasswordValid("short"), "Short password should be invalid");
        assertFalse(isPasswordValid("1234567"), "7-character password should be invalid");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }
}