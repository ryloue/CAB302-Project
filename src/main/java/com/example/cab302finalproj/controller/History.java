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

/**
 * Controller class for managing the history view of prompts in the application. */
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

    /**
     * Initialises the controller after its root element has been completely processed.
     * Sets up the UI components, loads initial prompt data, and configures event listeners.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param resourceBundle The resources used to localize the root object, or null if not localized
     */
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

    /**
     * Loads and displays prompts for the current user, organizing them into recent and weekly categories.
     * Recent prompts are the first 3 most recent prompts, while weekly prompts are the remainder
     * from the past 7 days.
     *
     * <p>This method clears existing containers and repopulates them with current data.
     * If no user is logged in (userId == -1), the method returns early without loading data.
     */
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

    /**
     * Refreshes the prompt data by reloading all prompts from the database.
     * This method is typically called when the data needs to be updated after
     * external changes or user actions.
     */
    public void refreshData() {
        loadPrompts();
    }

    /**
     * Performs a search through the user's prompts based on the provided search term.
     * If the search term is empty, normal prompt loading is restored.
     *
     * @param searchTerm the text to search for within prompts
     */
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

    /**
     * Displays a message in the recent prompts container when no search results are found.
     *
     * @param message the message text to display to the user
     */
    private void showMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        recentPromptsContainer.getChildren().add(messageLabel);
    }

    /**
     * Creates and displays interactive buttons for a list of prompts in the specified container.
     * Each button shows a shortened version of the prompt text and includes a tooltip
     * with the full prompt text. Clicking a button shows the detailed response.
     *
     * @param prompts the list of {@link Prompt} objects to display
     * @param container the {@link VBox} container where prompt buttons will be added
     */
    private void displayPrompts(List<Prompt> prompts, VBox container) {
        for (Prompt prompt : prompts) {
            Button promptButton = new Button(prompt.getShortPrompt());
            promptButton.setPrefWidth(130);
            promptButton.setStyle("-fx-background-color: #ffd500; -fx-background-radius: 10;");

            Tooltip tooltip = new Tooltip(prompt.getPromptText()); // Use full prompt text
            promptButton.setTooltip(tooltip);

            promptButton.setOnAction(event -> {
                selectedPrompt = prompt;
                showPromptDetails(prompt);
            });

            container.getChildren().add(promptButton);
        }
    }

    /**
     * Displays the detailed response text of the selected prompt in the scroll pane.
     *
     * @param prompt the {@link Prompt} object whose response details should be displayed
     */
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