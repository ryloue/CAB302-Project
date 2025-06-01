package com.example.cab302finalproj.controller;

import com.example.cab302finalproj.MainLayout;
import com.example.cab302finalproj.model.API_AI;
import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class for managing flashcard functionality in the application.
 * This class handles the creation, display, and navigation of AI-generated flashcards
 * based on user notes stored in the database.*/
public class Flashcards {
    public static MainLayout mainLayout;
    @FXML
    private Pane frontCard;
    @FXML
    private Pane backCard;
    @FXML
    private MenuItem Note1;
    @FXML
    private Text backCardText;
    @FXML
    private Text frontCardText;
    @FXML
    public Text FrontText;
    @FXML
    public Text BackText;
    private List<String> flashcards = new ArrayList<>();
    private int currentIndex = 0;

    @FXML
    private MenuButton notesMenuButton; // Add fx:id in FXML for this

    /**
     * Initialises the controller after the FXML file has been loaded.
     * Sets up the notes menu by populating it with available user notes from the database.
     */
    public void initialize() {
        try {
            populateNotesMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the notes menu button with menu items for each note belonging to the current user.
     * Each menu item, when clicked, will generate flashcards from the corresponding note content.
     *
     * @throws SQLException if there is an error accessing the database or executing the query
     */
    @FXML
    private void populateNotesMenu() throws SQLException {
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM DashNotes WHERE userId = ?";

        try (PreparedStatement statement = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        System.out.println("called");
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String titleNote = rs.getString("label");
                String notes = rs.getString("notes");

                MenuItem item = new MenuItem(titleNote);
                item.setOnAction(event -> {
                    try {
                        generateFlashcardsFromNote(notes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                notesMenuButton.getItems().add(item);
            }
        }
    }

    /**
     * Generates flashcards from the provided note content using AI processing.
     * This method runs the AI call on a separate thread to avoid blocking the UI,
     * then updates the flashcard display on the JavaFX Application Thread.
     *
     * <p>The AI is instructed to generate flashcards with answers limited to 15 words maximum.
     *
     * @param notes the note content to generate flashcards from
     */
    private void generateFlashcardsFromNote(String notes) {
        new Thread(() -> {
            try {
                String prompt = "Generate flashcards with maximum 15 words answer from the following note:\n\n" + notes;
                String response = API_AI.Call_Ai(prompt);

                // Simple parsing assumption (replace with GSON logic if AI response is JSON structured)
                Platform.runLater(() -> {
                    updateFlashcardUI(response);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Updates the flashcard user interface with the AI-generated response.
     * Parses the JSON response from the AI, extracts flashcard content,
     * and initializes the flashcard display with the first card.
     *
     * @param aiResponse the JSON response from the AI containing generated flashcards
     */
    private void updateFlashcardUI(String aiResponse) {
        // Parse the JSON
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(aiResponse, JsonObject.class);
        String generatedText = jsonObject.get("response").getAsString();

        // Split by double newline to separate each flashcard
        flashcards = Arrays.asList(generatedText.split("\\n\\n"));
        currentIndex = 0;

        showFlashcard(currentIndex);
    }

    /**
     * Displays the flashcard at the specified index.
     * Parses the flashcard content to separate the question (front) and answer (back),
     * then updates the UI components accordingly.
     * @param index the index of the flashcard to display
     */
    private void showFlashcard(int index) {
        if (flashcards.isEmpty() || index < 0 || index >= flashcards.size()) return;

        String card = flashcards.get(index);
        String[] lines = card.split("\\n", 2);
        if (lines.length == 2) {
            String title = lines[0].replaceFirst("Flashcard \\d+: ", "").trim();
            String content = lines[1].replaceFirst("^-\\s*", "").trim();

            frontCardText.setText("");
            FrontText.setText(title);
            backCardText.setText("");
            BackText.setText(content);
        } else {
            frontCardText.setText("");
            FrontText.setText("Malformed flashcard");
            backCardText.setText("");
            BackText.setText(card);
        }

        frontCard.setVisible(true);
        backCard.setVisible(false);
    }

    /**
     * Navigates to the next flashcard in the sequence.
     * If the current card is the last one, this method has no effect.
     */
    @FXML
    private void nextCard() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            showFlashcard(currentIndex);
        }
    }

    /**
     * Navigates to the previous flashcard in the sequence.
     * If the current card is the first one, this method has no effect.
     */
    @FXML
    private void previousCards() {
        if (currentIndex > 0) {
            currentIndex--;
            showFlashcard(currentIndex);
        }
    }

    public void selectNote() throws SQLException {
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT titleNo, notes FROM flashcards WHERE fromID = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, userId);
            try(ResultSet rs = statement.executeQuery()){
                while (rs.next()) {
                    String titleNote = rs.getString("titleNote");
                    String notes = rs.getString("notes");
                    backCardText.setText(titleNote);
                    frontCardText.setText(notes);
                }
            }
        }

    }

    /**
     * Animates the flipping of flashcards between front and back sides.
     * Creates a smooth scale transition effect that simulates a card flip animation.
     */
    public void flipFlashCards(){
        backCard.setVisible(!frontCard.isVisible());
        if (frontCard.isVisible()){
            ScaleTransition scaleCardIn = new ScaleTransition(Duration.seconds(0.5), frontCard);
            scaleCardIn.setToX(0.8);
            scaleCardIn.setToY(0.8);
            scaleCardIn.setOnFinished(event ->{


                ScaleTransition scaleCardOut = new ScaleTransition(Duration.seconds(0.2), frontCard);
                scaleCardOut.setToX(1.2);
                scaleCardOut.setToY(1.2);
                scaleCardOut.setOnFinished(eventbounce-> {
                    ScaleTransition settleCardIn = new ScaleTransition(Duration.seconds(0.15), frontCard);
                    settleCardIn.setToX(1);
                    settleCardIn.setToY(1);
                    settleCardIn.setOnFinished(eventswitchcards->{
                        ScaleTransition waitCard = new ScaleTransition(Duration.seconds(1), frontCard);
                        waitCard.setToX(1);
                        waitCard.setToY(1);
                        frontCard.setVisible(false);
                        backCard.setVisible(true);
                    });
                    settleCardIn.play();

                });
                scaleCardOut.play();

            });
            scaleCardIn.play();
        }
        else {
            ScaleTransition scaleCardOut = new ScaleTransition(Duration.seconds(0.5), backCard);
            scaleCardOut.setToX(0.8);
            scaleCardOut.setToY(0.8);
            scaleCardOut.setOnFinished(event ->{


                ScaleTransition scaleCardIn = new ScaleTransition(Duration.seconds(0.2), backCard);
                scaleCardIn.setToX(0.8);
                scaleCardIn.setToY(0.8);
                scaleCardIn.setToX(1.2);
                scaleCardIn.setToY(1.2);
                scaleCardIn.setOnFinished(eventbounce-> {
                   ScaleTransition settleCardIn = new ScaleTransition(Duration.seconds(0.15), backCard);
                   settleCardIn.setToX(1);
                   settleCardIn.setToY(1);
                    settleCardIn.setOnFinished(eventswitchcards->{
                        ScaleTransition waitCard = new ScaleTransition(Duration.seconds(1), backCard);
                        waitCard.setToX(1);
                        waitCard.setToY(1);
                        frontCard.setVisible(true);
                        backCard.setVisible(false);
                    });
                   settleCardIn.play();
                });
                scaleCardIn.play();
            });
            scaleCardOut.play();
        }


    }

}
