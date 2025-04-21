module com.example.cab302finalproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.example.cab302finalproj to javafx.fxml;
    opens com.example.cab302finalproj.Modules to com.fasterxml.jackson.databind;
    exports com.example.cab302finalproj;
}