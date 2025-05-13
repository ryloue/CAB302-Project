package com.example.cab302finalproj.model;

import java.sql.*;


public class DatabaseManager {
    // Database URL
    private static final String DB_URL = "jdbc:sqlite:userdb.sqlite";

    // Singleton instance
    private static DatabaseManager instance;

    // Connection object
    private Connection connection;


    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);

            initializeDatabase();

            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        } else {
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    instance.connection = DriverManager.getConnection(DB_URL);
                }
            } catch (SQLException e) {
                System.err.println("Error checking connection state: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return instance;
    }


    private void initializeDatabase() throws SQLException {
        // Create users table
        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
        }
        String createFlashcardTable =
                "CREATE TABLE IF NOT EXISTS flashcards(" +
                        "fromId INTEGER, " +
                        "titleNote TEXT, " +
                        "notes TEXT, " +
                        "FOREIGN KEY (fromId) REFERENCES users(id)" +
                        ")";
        try (Statement cfct = connection.createStatement()){
            cfct.execute(createFlashcardTable);
        }

    }


    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection reestablished in getConnection().");
            }
        } catch (SQLException e) {
            System.err.println("Error getting database connection: " + e.getMessage());
            e.printStackTrace();
        }

        return connection;
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}