package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;

public class Dashboard {

    @FXML
    private TextArea notesArea;

    @FXML
    private Button addMoreButton;

    @FXML
    private TextField searchField;

    @FXML
    private HBox buttonContainer;

    private int buttonCount = 1;

    @FXML
    public void handleAddMore() {
        // Subtract 1 to exclude the "Add More" button itself
        int existingButtons = buttonContainer.getChildren().size() - 1;

        if (existingButtons >= 6) {
            System.out.println("Maximum of 6 buttons reached.");
            return; // Stop adding more
        }

        Button newButton = new Button("Button " + buttonCount++);
        newButton.setStyle("-fx-background-color: #FDC500; -fx-background-radius: 10;");

        newButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                renameButton(newButton, event);
            }
        });

        buttonContainer.getChildren().add(newButton);
    }

    private void renameButton(Button button, MouseEvent event) {
        TextField renamer = new TextField(button.getText());
        renamer.setPrefWidth(button.getWidth());

        int index = buttonContainer.getChildren().indexOf(button);
        buttonContainer.getChildren().set(index, renamer);

        renamer.setOnAction(e -> {
            button.setText(renamer.getText());
            buttonContainer.getChildren().set(index, button);
        });

        renamer.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                button.setText(renamer.getText());
                buttonContainer.getChildren().set(index, button);
            }
        });

        // Optional: request focus for immediate typing
        renamer.requestFocus();
    }
}