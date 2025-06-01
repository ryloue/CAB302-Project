package com.example.cab302finalproj.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for managing Prompt entities in the database.
 */
public class PromptDAO {

    /**
     * Adds a new prompt record to the database with the current timestamp.
     *
     * @param userId the ID of the user who created the prompt
     * @param promptText the original text prompt sent to the AI or the file name
     * @param responseText the AI-generated response to the prompt
     *
     * @throws IllegalArgumentException if userId is negative or if promptText or responseText is null
     */
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

    /**
     * Retrieves all prompts for a specific user created within a specified number of days ago.
     * The results are ordered by creation date in descending order.
     *
     * @param userId the ID of the user whose prompts to retrieve
     * @param daysAgo the number of days to look back from the current date (e.g., 7 for last week)
     * @return a List of Prompt objects matching the criteria, or an empty list if none found
     *
     * @throws IllegalArgumentException if userId is negative or daysAgo is negative
     */
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

    /**
     * Searches for prompts belonging to a specific user that contain the specified search term
     * in their prompt text.
     *
     * @param userId the ID of the user whose prompts to search
     * @param searchTerm the text to search for within prompt texts (case-insensitive)
     * @return a List of Prompt objects whose prompt_text contains the search term,
     *         or an empty list if no matches are found
     *
     * @throws IllegalArgumentException if userId is negative
     */
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