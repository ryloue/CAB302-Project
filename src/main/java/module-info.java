module com.example.cab302finalproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;

    opens com.example.cab302finalproj to javafx.fxml;
    opens com.example.cab302finalproj.Modules to com.fasterxml.jackson.databind;
    exports com.example.cab302finalproj;
    exports com.example.cab302finalproj.model;
    opens com.example.cab302finalproj.model to javafx.fxml;
    exports com.example.cab302finalproj.controller;
    opens com.example.cab302finalproj.controller to javafx.fxml;
}