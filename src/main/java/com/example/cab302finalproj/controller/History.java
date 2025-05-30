package com.example.cab302finalproj.controller;

import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.Prompt;
import com.example.cab302finalproj.model.PromptDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class History implements Initializable {
    @FXML
    private TextField searchField;

    @FXML
    private VBox recentPromptsContainer;

    @FXML
    private VBox weekPromptsContainer;

    private Prompt selectedPrompt;
    private boolean searchActive = false;

    @FXML
    private ScrollPane scrollLabelPane;

    @FXML
    private Label scrollLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPrompts();

        scrollLabelPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
        """);

        scrollLabel.setStyle("""
            -fx-text-fill: black;
        """);

        scrollLabelPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollLabelPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchPrompts(newValue.trim());
        });
    }

    private void loadPrompts() {
        recentPromptsContainer.getChildren().clear();
        weekPromptsContainer.getChildren().clear();

        int userId = CurrentUser.getCurrentUserId();
        if (userId == -1) {
            return;
        }

        List<Prompt> lastWeekPrompts = PromptDAO.getPromptsByDate(userId, 7);

        List<Prompt> recent = lastWeekPrompts.size() > 3
                ? lastWeekPrompts.subList(0, 3)
                : lastWeekPrompts;

        List<Prompt> week = lastWeekPrompts.size() > 3
                ? lastWeekPrompts.subList(3, lastWeekPrompts.size())
                : new ArrayList<>();

        displayPrompts(recent, recentPromptsContainer);
        displayPrompts(week, weekPromptsContainer);
    }

    public void refreshData() {
        loadPrompts();
    }

    private void searchPrompts(String searchTerm) {
        recentPromptsContainer.getChildren().clear();
        weekPromptsContainer.getChildren().clear();

        if (searchTerm.isEmpty()) {
            searchActive = false;
            loadPrompts();
            return;
        }

        searchActive = true;

        int userId = CurrentUser.getCurrentUserId();

        List<Prompt> searchResults = PromptDAO.searchPrompts(userId, searchTerm);

        if (searchResults.isEmpty()) {
            showMessage("No results");
        } else {

            displayPrompts(searchResults, recentPromptsContainer);
        }
    }

    private void showMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        recentPromptsContainer.getChildren().add(messageLabel);
    }

    private void displayPrompts(List<Prompt> prompts, VBox container) {
        for (Prompt prompt : prompts) {
            Button promptButton = new Button(prompt.getShortPrompt());
            promptButton.setPrefWidth(130);
            promptButton.setStyle("-fx-background-color: #ffd500; -fx-background-radius: 10;");

            promptButton.setOnAction(event -> {
                selectedPrompt = prompt;
                showPromptDetails(prompt);
            });

            container.getChildren().add(promptButton);
        }
    }

    private void showPromptDetails(Prompt prompt) {
        scrollLabel.setStyle("""
            -fx-text-fill: black !important;
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-background-color: transparent;
        """);

        scrollLabel.setText("AI Response:\n" + prompt.getResponseText());

        scrollLabelPane.setVisible(true);
    }
}