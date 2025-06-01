package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class Login {

    @FXML
    private AnchorPane LoginContent;

    /**
     * Opens the default system browser and navigates to the specified URL
     * when the hyperlink is clicked.
     */
    @FXML
    private void handleLinkClick() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the login action. Assumes authentication has succeeded,
     * loads the MainLayout.fxml view, and sets it as the current scene.
     *
     * @param event the ActionEvent triggered by clicking the login button
     * @throws IOException if MainLayout.fxml cannot be loaded
     */
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
        Parent root = loader.load();

        // Retrieve the current stage from the event source and set the new scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();
    }

    /**
     * Navigates to the SignUp view by replacing the current content of the login pane.
     * Loads SignUp.fxml and sets it as the children of LoginContent.
     */
    @FXML
    private void GotoSignUp() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/SignUp.fxml")));
            LoginContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
