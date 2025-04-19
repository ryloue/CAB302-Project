package com.example.cab302finalproj.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class sqLiteConnection {
    private static Connection instance = null;

    private sqLiteConnection() {
        String url = "jdbc:sqlite:accounts.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new sqLiteConnection();
        }
        return instance;
    }
}