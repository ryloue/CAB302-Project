package com.example.cab302finalproj.model;

import java.util.Objects;

/**
 * Represents a prompt-response pair in the system, storing AI conversation data
 * associated with a specific user.
 */
public class Prompt {
    private int id;
    private int userId;
    private String promptText;
    private String responseText;
    private String createdAt;

    /**
     * Constructs a new Prompt object with all required fields.
     *
     * @param id the unique identifier for this prompt record
     * @param userId the ID of the user who created this prompt
     * @param promptText the original text prompt sent to the AI
     * @param responseText the AI-generated response to the prompt
     * @param createdAt the timestamp when this prompt was created (as a string)
     *
     * @throws NullPointerException if promptText, responseText, or createdAt is null
     */
    public Prompt(int id, int userId, String promptText, String responseText, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.promptText = promptText;
        this.responseText = responseText;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier for this prompt.
     *
     * @return the prompt's unique ID
     */
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    /**
     * Returns the original text prompt that was sent to the AI.
     *
     * @return the full prompt text
     */
    public String getPromptText() {
        return promptText;
    }

    /**
     * Returns the AI-generated response to the prompt.
     *
     * @return the full response text from the AI
     */
    public String getResponseText() {
        return responseText;
    }

    /**
     * Returns the timestamp when this prompt was created.
     *
     * @return the creation timestamp as a string
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns a shortened version of the prompt text for display purposes.
     * If the prompt text is 30 characters or less, returns the full text.
     * Otherwise, returns the first 27 characters followed by "...".
     *
     * @return a truncated version of the prompt text suitable for UI display
     */
    public String getShortPrompt() {
        if (promptText.length() <= 30) {
            return promptText;
        } else {
            return promptText.substring(0, 27) + "...";
        }
    }

    /**
     * Returns a string representation of this Prompt object.
     * This method delegates to getShortPrompt() to provide a concise
     * representation suitable for display in lists or UI components.
     *
     * @return the shortened prompt text
     *
     * @see #getShortPrompt()
     */
    @Override
    public String toString() {
        return getShortPrompt();
    }

    /**
     * Compares this Prompt object with another object for equality.
     *
     * @param o the object to compare with this Prompt
     * @return true if the objects are equal (same ID), false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prompt prompt = (Prompt) o;
        return id == prompt.id;
    }

    /**
     * Returns a hash code value for this Prompt object.
     * The hash code is based solely on the ID field, which is consistent
     * with the equals() method implementation.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
