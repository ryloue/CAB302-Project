package com.example.cab302finalproj.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Data Access Object for user-related database operations, including registration,
 * authentication, and validation checks.
 */
public class UserDAO {

    /**
     * Registers a new user by hashing the provided password and inserting the user record
     * into the database. If registration is successful, sets the current user ID and initializes
     * a corresponding flashcard entry for the new user.
     *
     * @param email    the user's email address (will be normalized to lowercase)
     * @param password the user's raw password (will be hashed using SHA-256)
     * @return true if the user was successfully registered; false otherwise
     * @throws SQLException              if a database error occurs during insertion
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static boolean registerUser(String email, String password) throws SQLException, NoSuchAlgorithmException {
        String hashedPassword = hashPassword(password);
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email.toLowerCase()); // Normalize email to lowercase
            pstmt.setString(2, hashedPassword);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    CurrentUser.setCurrentUserId(userId);
                    String insertFlashcardSQL = "INSERT INTO flashcards (fromId) VALUES (?)";
                    try (PreparedStatement flashcardStmt = conn.prepareStatement(insertFlashcardSQL)) {
                        flashcardStmt.setInt(1, userId);
                        CurrentUser.setCurrentUserId(userId);
                        flashcardStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("User insertion failed, no ID obtained.");
                }
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLException("User with this email already exists");
            } else {
                throw e;
            }
        }
    }

    /**
     * Authenticates a user by comparing the stored hashed password with the hash of the provided password.
     * If authentication succeeds, sets the current user ID.
     *
     * @param email    the user's email address (will be normalized to lowercase)
     * @param password the user's raw password (will be hashed and compared)
     * @return true if the email and password match an existing user; false otherwise
     * @throws SQLException              if a database error occurs during the lookup
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static boolean authenticateUser(String email, String password) throws SQLException, NoSuchAlgorithmException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT id, password FROM users WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.toLowerCase()); // Normalize email to lowercase
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    String hashedPassword = hashPassword(password);
                    int userId = rs.getInt("id");
                    CurrentUser.setCurrentUserId(userId);
                    return storedHashedPassword.equals(hashedPassword);
                }
                return false;
            }
        }
    }

    /**
     * Checks if a user with the specified email already exists in the database.
     *
     * @param email the email address to check (will be normalized to lowercase)
     * @return true if a user with the given email exists; false otherwise
     * @throws SQLException if a database error occurs during the query
     */
    public static boolean userExists(String email) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.toLowerCase()); // Normalize email to lowercase
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    /**
     * Hashes a plain-text password using SHA-256 and returns the hexadecimal representation
     * of the resulting hash.
     *
     * @param password the plain-text password to hash
     * @return a hexadecimal string representing the SHA-256 hash of the input password
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
