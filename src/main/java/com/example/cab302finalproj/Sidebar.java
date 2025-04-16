package com.example.cab302finalproj;

import javafx.fxml.FXML;

class Navigator {
    public static MainLayout mainLayout;
}
public class Sidebar {


    @FXML
    private void handleHomeClick() {
        Navigator.mainLayout.loadPage("Home.fxml");
    }

    @FXML
    private void handleDashboardClick() {
        Navigator.mainLayout.loadPage("Dashboard.fxml");
    }
}
