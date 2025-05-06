package com.example.cab302finalproj.controller;

import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForgotPassword {
    @FXML
    private TextField emailAddress;

    @FXML
    private Button resetPass;

    @FXML
    private TextField accessCode;

    @FXML
    private Text accessCodeText;

    public void sendResetPass(){
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT email FROM users WHERE email = ?";

        String email = emailAddress.getText();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String sqlEmail = rs.getString("email");
                    if (email.equals(sqlEmail)){
                        int randomNum = randomNumGen();
                        accessCodeText.setVisible(true);
                        accessCode.setVisible(true);
                        sendMail(randomNum, email);
                    }

                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

    private void sendMail(int randomInt, String recieverEmail){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "in-v3.mailjet.com");
        properties.put("mail.smtp.port", "587");
        String senderMail = "cabcoders@gmail.com";
        String senderAPI = "b54f8c6032d89c86d7d24df32d0caee1";
        String senderAPISecretKey = "1c3665ebfda197d7a31631abfdeaa72e";
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderAPI, senderAPISecretKey);
            }
        });
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderMail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recieverEmail));
            message.setSubject("Your Password Reset Code Is:");
            message.setText(randomInt + "\nDo Not Share This With Anybody.\n\nThank You, \nThe CAB Coding Team");

            Transport.send(message);

        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please include the valid email that was used to sign up with.");
            alert.showAndWait();
        }
    }
    private int randomNumGen(){
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }
}
