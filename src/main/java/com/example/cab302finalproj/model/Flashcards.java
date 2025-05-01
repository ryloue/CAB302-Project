package com.example.cab302finalproj.model;

import com.example.cab302finalproj.MainLayout;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;


public class Flashcards {
    public static MainLayout mainLayout;
    @FXML
    private Pane frontCard;
    @FXML
    private Pane backCard;
    @FXML
    private MenuItem Note1;
    @FXML
    private Text backCardText;
    @FXML
    private Text frontCardText;

    public void selectNote() throws SQLException {
        int userId = CurrentUser.getCurrentUserId();
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT titleNote, notes FROM flashcards WHERE fromID = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, userId);
            try(ResultSet rs = statement.executeQuery()){
                while (rs.next()) {
                    String titleNote = rs.getString("titleNote");
                    String notes = rs.getString("notes");
                    backCardText.setText(titleNote);
                    frontCardText.setText(notes);
                }
            }
        }

    }
    public void flipFlashCards(){
        backCard.setVisible(!frontCard.isVisible());
        if (frontCard.isVisible()){
            ScaleTransition scaleCardIn = new ScaleTransition(Duration.seconds(0.5), frontCard);
            scaleCardIn.setToX(0.8);
            scaleCardIn.setToY(0.8);
            scaleCardIn.setOnFinished(event ->{


                ScaleTransition scaleCardOut = new ScaleTransition(Duration.seconds(0.2), frontCard);
                scaleCardOut.setToX(1.2);
                scaleCardOut.setToY(1.2);
                scaleCardOut.setOnFinished(eventbounce-> {
                    ScaleTransition settleCardIn = new ScaleTransition(Duration.seconds(0.15), frontCard);
                    settleCardIn.setToX(1);
                    settleCardIn.setToY(1);
                    settleCardIn.setOnFinished(eventswitchcards->{
                        ScaleTransition waitCard = new ScaleTransition(Duration.seconds(1), frontCard);
                        waitCard.setToX(1);
                        waitCard.setToY(1);
                        frontCard.setVisible(false);
                        backCard.setVisible(true);
                    });
                    settleCardIn.play();

                });
                scaleCardOut.play();

            });
            scaleCardIn.play();
        }
        else {
            ScaleTransition scaleCardOut = new ScaleTransition(Duration.seconds(0.5), backCard);
            scaleCardOut.setToX(0.8);
            scaleCardOut.setToY(0.8);
            scaleCardOut.setOnFinished(event ->{


                ScaleTransition scaleCardIn = new ScaleTransition(Duration.seconds(0.2), backCard);
                scaleCardIn.setToX(0.8);
                scaleCardIn.setToY(0.8);
                scaleCardIn.setToX(1.2);
                scaleCardIn.setToY(1.2);
                scaleCardIn.setOnFinished(eventbounce-> {
                   ScaleTransition settleCardIn = new ScaleTransition(Duration.seconds(0.15), backCard);
                   settleCardIn.setToX(1);
                   settleCardIn.setToY(1);
                    settleCardIn.setOnFinished(eventswitchcards->{
                        ScaleTransition waitCard = new ScaleTransition(Duration.seconds(1), backCard);
                        waitCard.setToX(1);
                        waitCard.setToY(1);
                        frontCard.setVisible(true);
                        backCard.setVisible(false);
                    });
                   settleCardIn.play();
                });
                scaleCardIn.play();
            });
            scaleCardOut.play();
        }


    }
    public void previousCards() {

    }
}
