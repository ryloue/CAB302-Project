module com.example.cab302finalproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.cab302finalproj to javafx.fxml;
    exports com.example.cab302finalproj;
}