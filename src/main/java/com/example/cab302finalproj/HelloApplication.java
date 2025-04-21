package com.example.cab302finalproj;

import com.example.cab302finalproj.model.DatabaseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database connection
        DatabaseManager.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Font.loadFont(getClass().getResourceAsStream("/fonts/MyFont.ttf"), 14);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        // Close database connection when application exits
        stage.setOnCloseRequest(event -> {
            DatabaseManager.getInstance().closeConnection();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}