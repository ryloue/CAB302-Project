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
    @FXML
    private ComboBox<Language> languageComboBox;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the file chooser default directory and extension filters.
     * Also loads the available languages into the languageComboBox.
     *
     * @param url the location used to resolve relative paths for the root object, or null if unknown
     * @param resourceBundle the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(ex1, ex2, ex3, ex4);
        // Load languages
        loadLanguages();
    }

    /**
     * Returns the file name without its extension. If the file is null, returns "Untitled".
     *
     * @param file the file whose name is to be extracted
     * @return the file name without extension, or "Untitled" if file is null
     */
    private String getFileName(File file) {
        if (file == null) {
            return "Untitled";
        }

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }

        return fileName;
    }

    /**
     * Retrieves the extension of the given file in lowercase.
     *
     * @param file the file for which to determine the extension
     * @return the file extension (without the dot) in lowercase, or an empty string if none
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Extracts all text content from the specified PDF file and appends it to File_Content.
     * Uses Apache PDFBox PDFTextStripper for text extraction.
     *
     * @param file the PDF file from which text will be extracted
     */
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

    /**
     * Handles the upload button action. Opens a file chooser dialog, allows the user to select a file,
     * then reads its contents depending on the file type (PDF or text). Updates fileNameLabel and File_Content.
     *
     * @param event the ActionEvent triggered by clicking the upload button
     */
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

    /**
     * Handles drag-over events on the designated drop target. If the dragged content includes files,
     * accepts the transfer mode as COPY.
     *
     * @param event the DragEvent triggered when the user drags files over the drop zone
     */
    @FXML
    public void dragFile(DragEvent event) {
        File_Content.setLength(0);
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    /**
     * Handles drop events when the user releases files onto the drop target.
     * Retrieves the first file from the dragboard, reads its content line by line into File_Content,
     * and updates fileNameLabel. Marks the drop operation as completed.
     *
     * @param event the DragEvent triggered when the user drops files onto the drop zone
     */
    @FXML
    public void dropFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            try {
                // Get the first file (you can loop through all files if needed)
                File file = dragboard.getFiles().get(0);
                selectedFile = file;
                // Update the label with file name
                fileNameLabel.setText(file.getName());

                // Read file content
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

    /**
     * Loads the list of supported languages from a JSON resource (languages.json) and populates the languageComboBox.
     * Uses Jackson ObjectMapper to deserialize the JSON into a List of Language objects.
     */
    private void loadLanguages() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/languages.json");
            List<Language> languages = mapper.readValue(inputStream, new TypeReference<List<Language>>() {});
            languageComboBox.getItems().addAll(languages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the preview button action. Loads the PreviewFile.fxml layout, sets the current File_Content
     * into the previewTextArea, and displays it in a new window.
     *
     * @param event the ActionEvent triggered by clicking the preview button
     */
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

    /**
     * JavaFX-friendly data structure to hold the AI response for summarization/translation.
     */
    public static class TranslationResponse {
        public String response;
        public boolean done;
        public String done_reason;
        // add other fields if needed
    }

    /**
     * Generates a summary of the current File_Content. Shows a modal loading dialog while the AI call is in progress.
     * Once the summary is returned, saves it via PromptDAO and displays it in a PreviewFile popup.
     */
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
                        """, File_Content);

                String json = API_AI.Call_Ai(prompt);
                Gson gson = new Gson();
                TranslationResponse result = gson.fromJson(json, TranslationResponse.class);
                String translatedText = result.response;

                String promptName = getFileName(selectedFile);
                PromptDAO.addPrompt(CurrentUser.getCurrentUserId(), promptName, translatedText);

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("PreviewFile.fxml"));
                        Scene scene = new Scene(loader.load());

                        // Get controller from loaded FXML
                        Home controller = loader.getController();
                        controller.previewTextArea.setText(translatedText);  // Set summarized text

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

    /**
     * Translates the current File_Content into the language selected in languageComboBox.
     * Displays a modal loading dialog while calling the AI. Once the translation is returned,
     * saves it via PromptDAO and displays it in a PreviewFile popup.
     */
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

                String json = API_AI.Call_Ai(prompt);
                Gson gson = new Gson();
                TranslationResponse result = gson.fromJson(json, TranslationResponse.class);
                String translatedText = result.response;

                String promptName = getFileName(selectedFile);
                PromptDAO.addPrompt(CurrentUser.getCurrentUserId(), promptName, translatedText);

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
