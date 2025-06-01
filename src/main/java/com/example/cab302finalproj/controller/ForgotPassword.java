package com.example.cab302finalproj.controller;

import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private TextField newPassword;

    @FXML
    private Text emailText;

    @FXML
    private Button resetPassButton;

    @FXML
    private Button sendCodeButton;

    @FXML
    private Text passwordText;

    @FXML
    private TextField accessCode;

    @FXML
    private Text accessCodeText;

    static int randNum = -1;

    /**
     * Initiates the password reset process by verifying that the provided email exists in the database.
     * If the email is found, generates a random verification code, updates the UI to show code and password fields,
     * sends the code via email, and stores the code for later verification.
     */
    public void sendResetPass() {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT email FROM users WHERE email = ?";
        String email = emailAddress.getText();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int randomNum = randomNumGen();

                    emailAddress.setVisible(false);
                    emailText.setVisible(false);
                    sendCodeButton.setVisible(false);

                    accessCodeText.setVisible(true);
                    accessCode.setVisible(true);
                    newPassword.setVisible(true);
                    passwordText.setVisible(true);
                    resetPassButton.setVisible(true);

                    sendMail(randomNum, email);
                    setNum(randomNum);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Email Does Not Exist", "Please Enter A Valid Email");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Email Does Not Exist", "Please Enter A Valid Email");
        }
    }

    /**
     * Stores the generated random verification code for later comparison.
     *
     * @param randomInt the randomly generated verification code
     */
    private void setNum(int randomInt) {
        randNum = randomInt;
    }

    /**
     * Verifies the user-entered access code against the stored verification code.
     * If the codes match and the new password meets length requirements, hashes the new password
     * and updates it in the database. Shows appropriate alerts on success or failure.
     */
    public void verifyNum() {
        try {
            int transCode = Integer.parseInt(accessCode.getText());
            String newPass = newPassword.getText();
            String hashNewPass = hashPassword(newPass);
            String email = emailAddress.getText();

            if (newPass.length() < 8) {
                showAlert(Alert.AlertType.ERROR, "Password Error", "Password must be at least 8 characters long.");
                return;
            }
            if (transCode == randNum) {
                Connection conn = DatabaseManager.getInstance().getConnection();
                String sql = "UPDATE users SET password = ? WHERE email = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, hashNewPass);
                    pstmt.setString(2, email);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Your password has been updated, returning to login");
                        handleLoginPage();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password, please try again later");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Code", "Entered verification code is incorrect.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please input a valid number");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email containing the verification code to the specified recipient using Mailjet SMTP.
     *
     * @param randomInt      the verification code to include in the email
     * @param recieverEmail  the recipient's email address
     */
    private void sendMail(int randomInt, String recieverEmail) {
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
        try {
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

    /**
     * Generates a random five-digit integer to be used as the verification code.
     *
     * @return a random integer between 10000 and 99999
     */
    private int randomNumGen() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }

    /**
     * Displays a JavaFX alert dialog with the specified type, title, and message.
     *
     * @param type    the type of alert (e.g., INFORMATION, ERROR)
     * @param title   the title of the alert window
     * @param message the content message to display in the alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hashes the provided plain-text password using SHA-256 and returns the hexadecimal string.
     *
     * @param password the plain-text password to hash
     * @return the SHA-256 hash of the password as a hexadecimal string
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Loads and displays the login page by setting the scene to Login.fxml.
     * Logs errors if the FXML cannot be loaded.
     */
    public void handleLoginPage() {
        try {
            Parent rootPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cab302finalproj/Login.fxml")));
            Stage stage = (Stage) resetPassButton.getScene().getWindow();
            stage.setScene(new Scene(rootPage));
            stage.show();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load FXML Login.fxml", e);
        }
    }
}
