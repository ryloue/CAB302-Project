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
            // Create and initialize the database connection
            connection = DriverManager.getConnection(DB_URL);

            // Create tables if they don't exist
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
    }


    public Connection getConnection() {
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