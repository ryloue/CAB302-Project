package com.example.cab302finalproj;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.net.URI;

public class Login {
    @FXML
    private Hyperlink linkHelp;

    @FXML
    private void handleLinkClick() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
