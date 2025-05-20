package com.example.cab302finalproj.model;

import java.util.Objects;

public class Prompt {
    private int id;
    private int userId;
    private String promptText;
    private String responseText;
    private String createdAt;

    public Prompt(int id, int userId, String promptText, String responseText, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.promptText = promptText;
        this.responseText = responseText;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getPromptText() {
        return promptText;
    }

    public String getResponseText() {
        return responseText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getShortPrompt() {
        if (promptText.length() <= 30) {
            return promptText;
        } else {
            return promptText.substring(0, 27) + "...";
        }
    }

    @Override
    public String toString() {
        return getShortPrompt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prompt prompt = (Prompt) o;
        return id == prompt.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
