package com.example.cab302finalproj;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUp {
    private static final Logger LOGGER = Logger.getLogger(SignUp.class.getName());
    public Hyperlink Login_Link;
    public AnchorPane SignUpContent;

    @FXML
    private void GotoLogin() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/Login.fxml")));
            SignUpContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading Login", e);
        }
    }


    @FXML
    private void handleSignUp(ActionEvent event) throws IOException {
        // Here you would normally validate inputs and create a new user account
        // For now, we'll just navigate to the dashboard

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
        Parent root = loader.load();

        // Get the current stage from the event
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();
    }
}
