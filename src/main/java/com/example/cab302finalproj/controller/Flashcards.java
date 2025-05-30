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

    public void initialize() {
        try {
            populateNotesMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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

    @FXML
    private void nextCard() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            showFlashcard(currentIndex);
        }
    }

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
