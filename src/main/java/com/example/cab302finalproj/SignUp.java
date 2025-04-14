package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public class SignUp {
    public Hyperlink Login_Link;
    public AnchorPane SignUpContent;
    @FXML
    private void GotoLogin() {
        try {
            AnchorPane newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/Login.fxml")));
            SignUpContent.getChildren().setAll(newPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
