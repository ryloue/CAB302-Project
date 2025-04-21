package com.example.cab302finalproj.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


public class UserDAO {

    public static boolean registerUser(String email, String password) throws SQLException, NoSuchAlgorithmException {
        String hashedPassword = hashPassword(password);
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLException("User with this email already exists");
            } else {
                throw e;
            }
        }
    }

    public static boolean authenticateUser(String email, String password) throws SQLException, NoSuchAlgorithmException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT password FROM users WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    String hashedPassword = hashPassword(password);

                    return storedHashedPassword.equals(hashedPassword);
                }
                return false;
            }
        }
    }

    public static boolean userExists(String email) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException {
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