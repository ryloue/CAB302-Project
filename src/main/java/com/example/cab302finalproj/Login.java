package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

public class Login {

    @FXML
    private AnchorPane LoginContent;

    @FXML
    private void handleLinkClick() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
