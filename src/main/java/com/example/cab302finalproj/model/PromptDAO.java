package com.example.cab302finalproj.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromptDAO {
    public static void addPrompt(int userId, String promptText, String responseText) {
        String sql = "INSERT INTO prompts(user_id, prompt_text, response_text, created_at) VALUES(?,?,?,datetime('now'))";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, promptText);
            pstmt.setString(3, responseText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding prompt: " + e.getMessage());
        }
    }

    public static List<Prompt> getPromptsByDate(int userId, int daysAgo) {
        String sql = "SELECT prompt_id, prompt_text, response_text, created_at FROM prompts " +
                "WHERE user_id = ? AND created_at >= datetime('now', ? || ' days') " +
                "ORDER BY created_at DESC";

        List<Prompt> prompts = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "-" + daysAgo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Prompt prompt = new Prompt(
                            rs.getInt("prompt_id"),
                            userId,
                            rs.getString("prompt_text"),
                            rs.getString("response_text"),
                            rs.getString("created_at")
                    );
                    prompts.add(prompt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving prompts by date: " + e.getMessage());
        }

        return prompts;
    }

    public static List<Prompt> searchPrompts(int userId, String searchTerm) {
        String sql = "SELECT prompt_id, prompt_text, response_text, created_at FROM prompts " +
                "WHERE user_id = ? AND prompt_text COLLATE NOCASE LIKE ? " +
                "ORDER BY created_at DESC";

        List<Prompt> prompts = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Prompt prompt = new Prompt(
                            rs.getInt("prompt_id"),
                            userId,
                            rs.getString("prompt_text"),
                            rs.getString("response_text"),
                            rs.getString("created_at")
                    );
                    prompts.add(prompt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching prompts: " + e.getMessage());
            e.printStackTrace();
        }

        return prompts;
    }
}