package com.example.cab302finalproj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.example.cab302finalproj.database.sqLiteAccountDAO;
import com.example.cab302finalproj.database.sqLiteConnection;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Font.loadFont(getClass().getResourceAsStream("/fonts/MyFont.ttf"), 14);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        accountDAO = new sqLiteAccountDAO();

    }
    private sqLiteAccountDAO accountDAO;
    public static void main(String[] args) {
        launch();
    }
}