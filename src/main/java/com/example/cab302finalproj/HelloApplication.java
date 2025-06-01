package com.example.cab302finalproj;

import com.example.cab302finalproj.model.DatabaseManager;
import com.example.cab302finalproj.model.Login;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    /**
     * Initializes the JavaFX application. Sets up the database connection, loads the login FXML,
     * registers an ENTER key handler to trigger login, loads a custom font, and handles application
     * shutdown by closing the database connection.
     *
     * @param stage the primary stage supplied by the JavaFX runtime
     * @throws IOException if the Login.fxml resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database connection
        DatabaseManager.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Login loginController = fxmlLoader.getController();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case ENTER:
                        loginController.handleLogin(new ActionEvent());
                        break;
                }
            }
        });

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

    /**
     * Main entry point when launching the application. Delegates to the JavaFX launch mechanism.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }
}
