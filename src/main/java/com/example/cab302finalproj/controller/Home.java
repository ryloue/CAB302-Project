package com.example.cab302finalproj.controller;


import com.example.cab302finalproj.HelloApplication;
import com.example.cab302finalproj.Modules.Language;
import com.example.cab302finalproj.model.API_AI;
import com.example.cab302finalproj.model.CurrentUser;
import com.example.cab302finalproj.model.PromptDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Home implements Initializable {
    private final FileChooser fileChooser = new FileChooser();
    private final FileChooser.ExtensionFilter ex1 = new FileChooser.ExtensionFilter("Pdf", "*.pdf");
    private final FileChooser.ExtensionFilter ex2 = new FileChooser.ExtensionFilter("Text File", "*.txt");
    private final FileChooser.ExtensionFilter ex3 = new FileChooser.ExtensionFilter("Word File", "*.docx");
    private final FileChooser.ExtensionFilter ex4 = new FileChooser.ExtensionFilter("Excel File", "*.csv");
    @FXML
    private Label fileNameLabel;
    private File selectedFile;
    @FXML
    private Label loadingLabel;
    @FXML
    StringBuilder File_Content = new StringBuilder();
    @FXML
    private TextArea previewTextArea;


    /**
     * @param url            is for getting the url
     * @param resourceBundle to allocate the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(ex1, ex2, ex3, ex4);
        // Load languages
        loadLanguages();
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase(); // Return extension in lowercase
        }
        return ""; // Return empty string if no extension
    }

    public void extractTextFromPDF(File file) {
        PDDocument document = null;
        try {
            // Load the PDF document
            document = PDDocument.load(file);

            // Initialize PDFTextStripper to extract text
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // Extract text from the entire document
            File_Content.append(pdfStripper.getText(document));
        } catch (IOException e) {
            // Handle any IOExceptions
            System.err.println("Error loading or processing PDF file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure that the document is closed after processing
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    System.err.println("Error closing PDF document: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    public void handleUpload(ActionEvent event) {
        File_Content.setLength(0);
        File file = fileChooser.showOpenDialog(new Stage());
        selectedFile = file;
        if (file != null) {  // Check if user didn't cancel the dialog
            try {
                fileNameLabel.setText(file.getName());
                Scanner sc = new Scanner(file);
                System.out.println(file.getName());

                String extension = getFileExtension(file);

                if (extension.equals("pdf")) {
                    System.out.println("pdf called");
                    extractTextFromPDF(file);

                } else if (extension.equals("text")) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        File_Content.append(line).append("\n");
                        System.out.println(line);
                    }
                }


                sc.close();
            } catch (FileNotFoundException e) {
                fileNameLabel.setText("Error loading file");

            }
        }
    }

    @FXML
    public void dragFile(DragEvent event) {
        File_Content.setLength(0);
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    public void dropFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            try {
                // Get the first file (you can loop through all files if needed)
                File file = dragboard.getFiles().getFirst();
                selectedFile = file;
                // Update the label with file name
                fileNameLabel.setText(file.getName());

                // Optional: Read file content
                try (Scanner scanner = new Scanner(file)) {

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        File_Content.append(line).append("\n");
                        System.out.println(line);
                    }
                }

                success = true;
            } catch (Exception e) {
                fileNameLabel.setText("Error processing file");
                e.printStackTrace();
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private ComboBox<Language> languageComboBox;


    private void loadLanguages() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/languages.json");
            List<Language> languages = mapper.readValue(inputStream, new TypeReference<List<Language>>() {
            });
            languageComboBox.getItems().addAll(languages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handlePreview(ActionEvent event) {
        try {


            // Load the FXML using the same controller
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("PreviewFile.fxml"));
            loader.setController(this); // <-- Important to reuse this controller
            Scene scene = new Scene(loader.load());


            previewTextArea.setText(File_Content.toString());

            // Show in a popup
            Stage previewStage = new Stage();
            previewStage.setTitle("File Preview");
            previewStage.setScene(scene);
            previewStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class TranslationResponse {
        public String response;
        public boolean done;
        public String done_reason;
        // add other fields if needed
    }




    public void Summerise() {
        Stage loadingStage = new Stage();
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        loadingBox.getChildren().add(new Label("Summarising, please wait..."));
        Scene loadingScene = new Scene(loadingBox, 250, 100);
        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("Loading");
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.show();

        new Thread(() -> {
            try {
                String prompt = String.format("""
                        You are a summarization assistant.
                        Summarize the following text :
                "%s"
                """,  File_Content);

                String json = API_AI.Translate(prompt);
                Gson gson = new Gson();
                TranslationResponse result = gson.fromJson(json, TranslationResponse.class);
                String translatedText = result.response;

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("PreviewFile.fxml"));
                        Scene scene = new Scene(loader.load());

                        // Get controller from loaded FXML
                        Home controller = loader.getController();
                        controller.previewTextArea.setText(translatedText);  // Set translated text

                        loadingStage.close();

                        Stage previewStage = new Stage();
                        previewStage.setTitle("Summerised File Preview");
                        previewStage.setScene(scene);
                        previewStage.show();
                    } catch (IOException e) {
                        loadingStage.close();
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                Platform.runLater(loadingStage::close);
                e.printStackTrace();
            }
        }).start();
    }






    public void Translate() {
        Stage loadingStage = new Stage();
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        loadingBox.getChildren().add(new Label("Translating, please wait..."));
        Scene loadingScene = new Scene(loadingBox, 250, 100);
        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("Loading");
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.show();

        new Thread(() -> {
            try {
                String prompt = String.format("""
                You are a translation assistant.
                
                Translate the following text to %s:
                
                "%s"
                """, languageComboBox.getValue(), File_Content);

                String json = API_AI.Translate(prompt);
                Gson gson = new Gson();
                TranslationResponse result = gson.fromJson(json, TranslationResponse.class);
                String translatedText = result.response;
                PromptDAO.addPrompt(CurrentUser.getCurrentUserId(), File_Content.toString(), translatedText);
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("PreviewFile.fxml"));
                        Scene scene = new Scene(loader.load());

                        // Get controller from loaded FXML
                        Home controller = loader.getController();
                        controller.previewTextArea.setText(translatedText);  // Set translated text

                        loadingStage.close();

                        Stage previewStage = new Stage();
                        previewStage.setTitle("Translated File Preview");
                        previewStage.setScene(scene);
                        previewStage.show();
                    } catch (IOException e) {
                        loadingStage.close();
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                Platform.runLater(loadingStage::close);
                e.printStackTrace();
            }
        }).start();
    }

}
