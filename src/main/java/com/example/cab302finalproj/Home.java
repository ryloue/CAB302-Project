package com.example.cab302finalproj;

import com.example.cab302finalproj.Modules.Language;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.InputStream;
import java.util.List;

public class Home {
    public void handleUpload(ActionEvent event) {
    }
    @FXML
    private ComboBox<Language> languageComboBox;

    @FXML
    public void initialize() {
        loadLanguages();
    }

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
