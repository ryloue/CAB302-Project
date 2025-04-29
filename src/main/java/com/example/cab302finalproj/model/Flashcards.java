package com.example.cab302finalproj.model;

import com.example.cab302finalproj.MainLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;


public class Flashcards {
    public static MainLayout mainLayout;
    public void handleFlashcardAnswerclick() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/Flashcards.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
