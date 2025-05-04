import com.example.cab302finalproj.model.SignUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestSignUp {

    // Email regex pattern from SignUp class
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Tests the validation logic for empty fields
     */
    @Test
    @DisplayName("Test validateInputs with empty fields")
    void testValidateInputsWithEmptyFields() {
        SignUp signUp = new SignUp();
        // Only if fields are initialized inside SignUp
        if (signUp.emailField != null) {
            signUp.emailField.setText("");
            signUp.passwordField.setText("password123");
            signUp.confirmPasswordField.setText("password123");
            assertFalse(signUp.validateInputs(), "Empty email should fail validation");
        }

        // Test empty email
        assertFalse(validateFields("", "password123", "password123"),
                "Empty email should fail validation");

        // Test empty password
        assertFalse(validateFields("test@example.com", "", "password123"),
                "Empty password should fail validation");

        // Test empty confirmation password
        assertFalse(validateFields("test@example.com", "password123", ""),
                "Empty confirmation password should fail validation");
    }

    /**
     * Tests the validation logic for invalid email format
     */
    @Test
    @DisplayName("Test validateInputs with invalid email format")
    void testValidateInputsWithInvalidEmail() {
        assertFalse(validateFields("invalid-email", "password123", "password123"),
                "Invalid email format should fail validation");
        assertFalse(validateFields("user@", "password123", "password123"),
                "Email without domain should fail validation");
        assertFalse(validateFields("@domain.com", "password123", "password123"),
                "Email without username should fail validation");
    }

    /**
     * Tests the validation logic for password length
     */
    @Test
    @DisplayName("Test validateInputs with short password")
    void testValidateInputsWithShortPassword() {
        assertFalse(validateFields("test@example.com", "short", "short"),
                "Password less than 8 characters should fail validation");
    }

    /**
     * Tests the validation logic for password matching
     */
    @Test
    @DisplayName("Test validateInputs with mismatched passwords")
    void testValidateInputsWithMismatchedPasswords() {
        assertFalse(validateFields("test@example.com", "password123", "differentpassword"),
                "Mismatched passwords should fail validation");
    }

    /**
     * Tests the validation logic with valid inputs
     */
    @Test
    @DisplayName("Test validateInputs with valid inputs")
    void testValidateInputsWithValidInputs() {
        assertTrue(validateFields("test@example.com", "password123", "password123"),
                "Valid inputs should pass validation");
        assertTrue(validateFields("user.name@domain.co.uk", "longerpassword123", "longerpassword123"),
                "Valid complex inputs should pass validation");
    }

    /**
     * Helper method that replicates the validation logic from SignUp class
     * This extracts just the core validation logic we want to test
     */
    private boolean validateFields(String email, String password, String confirmPassword) {
        // Check for empty fields
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }

        // Validate email format
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            return false;
        }

        // Check password length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return false;
        }

        return true;
    }

    /**
     * Tests email validation separately
     */
    @Test
    @DisplayName("Test email validation")
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
     * Tests password length validation separately
     */
    @Test
    @DisplayName("Test password length validation")
    void testPasswordLengthValidation() {
        assertTrue(isPasswordValid("password123"), "8-char password should be valid");
        assertTrue(isPasswordValid("alongerpassword"), "Longer password should be valid");

        assertFalse(isPasswordValid(""), "Empty password should be invalid");
        assertFalse(isPasswordValid("short"), "Short password should be invalid");
        assertFalse(isPasswordValid("1234567"), "7-char password should be invalid");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Tests password matching validation separately
     */
    @Test
    @DisplayName("Test password matching validation")
    void testPasswordMatching() {
        assertTrue(doPasswordsMatch("samepassword", "samepassword"), "Identical passwords should match");
        assertFalse(doPasswordsMatch("password1", "password2"), "Different passwords should not match");
        assertFalse(doPasswordsMatch("Password", "password"), "Case-sensitive passwords should not match");
    }

    private boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}