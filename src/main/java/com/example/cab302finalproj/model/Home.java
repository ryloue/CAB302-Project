package com.example.cab302finalproj.model;

import com.example.cab302finalproj.Modules.Language;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
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


    /**
     * @param url is for getting the url
     * @param resourceBundle to allocate the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(ex1,ex2,ex3,ex4);
        // Load languages
        loadLanguages();
    }

    @FXML
    public void handleUpload(ActionEvent event) {
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {  // Check if user didn't cancel the dialog
            try {
                fileNameLabel.setText(file.getName());
                Scanner sc = new Scanner(file);
                System.out.println(file.getName());
                while (sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
                sc.close();
            } catch (FileNotFoundException e) {
                fileNameLabel.setText("Error loading file");

            }
        }
    }

    @FXML
    public  void dragFile(DragEvent event){
        Dragboard dragboard = event.getDragboard();
        if(dragboard.hasFiles() ){
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    public  void dropFile(DragEvent event){
    Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            try {
                // Get the first file (you can loop through all files if needed)
                File file = dragboard.getFiles().getFirst();

                // Update the label with file name
                fileNameLabel.setText(file.getName());

                // Optional: Read file content
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
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
            List<Language> languages = mapper.readValue(inputStream, new TypeReference<List<Language>>() {});
            languageComboBox.getItems().addAll(languages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
