package com.example.cab302finalproj.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


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


    public void sendResetPass(){
        String email = emailAddress.getText();
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);
        System.out.println(randomNum);
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
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Your Password Reset Code Is:");
            message.setText(randomNum + "\nDo Not Share This With Anybody.\n\nThank You, \nThe CAB Coding Team");

            Transport.send(message);

        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please include the valid email that was used to sign up with.");
            alert.showAndWait();
        }
    }

}
