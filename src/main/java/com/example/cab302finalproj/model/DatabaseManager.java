package com.example.cab302finalproj.model;

import java.sql.*;

/**
 * A database manager class that handles SQLite database connections
 * and table initialisation for the application. This class manages user data,
 * flashcards, dashboard notes and history.
 */
public class DatabaseManager {
    // Database URL
    private static final String DB_URL = "jdbc:sqlite:userdb.sqlite";

    // Singleton instance
    private static DatabaseManager instance;

    // Connection object
    private Connection connection;

    /**
     * Private constructor that initialises the database connection and creates
     * necessary tables.
     *
     * @implNote Uses SQLite database located at "userdb.sqlite". */
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

    /**
     * Returns the instance of DatabaseManager. If the instance doesn't exist,
     * it creates a new one. If the existing connection is closed, it reestablishes
     * the connection.
     *
     * @return the DatabaseManager instance
     * @throws RuntimeException if there's an error checking or reestablishing the connection
     *
     * @apiNote This method is thread-safe due to the synchronized keyword
     * @apiNote The method automatically handles connection recovery if the database
     *          connection has been closed
     */
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

    /**
     * Initialises the database by creating all necessary tables if they don't exist.
     * This method creates the users, flashcards, and DashNotes tables with proper
     * foreign key relationships.
     *
     * @throws SQLException if there's an error creating any of the tables
     *
     * @implNote Calls verifyDashNotesTable() to ensure table schema integrity
     */
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

        String createDashNotesTable =
                "CREATE TABLE IF NOT EXISTS DashNotes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userId INTEGER NOT NULL, " +
                        "label TEXT NOT NULL, " +
                        "notes TEXT, " +
                        "FOREIGN KEY (userId) REFERENCES users(id)" +
                        ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createDashNotesTable);
        }

        // Check and recreate DashNotes table if needed
        verifyDashNotesTable();
    }


    /**
     * Returns the current database connection, creating a new one if the existing
     * connection is null or closed. This method ensures that a valid connection
     * is always available for database operations.
     *
     * @return a valid Connection object to the SQLite database
     * @throws RuntimeException if there's an error establishing the database connection
     */
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

    /**
     * Closes the database connection if it's currently open. This method is
     * called when shutting down the application to properly release database
     * resources.
     */
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

    /**
     * Drops the DashNotes table from the database. This is a utility method
     * used internally for table schema management and migration.
     *
     * @throws SQLException if there's an error executing the DROP TABLE statement
     */
    private void dropDashNotesTable() throws SQLException {
        String dropSQL = "DROP TABLE IF EXISTS DashNotes";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropSQL);
            System.out.println("DashNotes table dropped.");
        }
    }

    /**
     * Verifies that the DashNotes table has the correct schema, particularly
     * checking for the existence of the 'notes' column. If the column is missing,
     * the method drops and recreates the table with the correct schema.
     *
     * @throws SQLException if there's an error checking the table schema or
     *                      recreating the table
     */
    private void verifyDashNotesTable() throws SQLException {
        boolean notesColumnExists = false;

        // Check for the 'notes' column in the 'DashNotes' table
        String checkSQL = "PRAGMA table_info(DashNotes)";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(checkSQL)) {
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("notes".equals(columnName)) {
                    notesColumnExists = true;
                    break;
                }
            }
        }

        // If the 'notes' column does not exist, drop and recreate the table
        if (!notesColumnExists) {
            System.out.println("Notes column missing. Recreating DashNotes table.");

            dropDashNotesTable();
            String createDashNotesTable =
                    "CREATE TABLE IF NOT EXISTS DashNotes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "userId INTEGER NOT NULL, " +
                            "label TEXT NOT NULL, " +
                            "notes TEXT, " +
                            "FOREIGN KEY (userId) REFERENCES users(id)" +
                            ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createDashNotesTable);
                System.out.println("DashNotes table recreated successfully.");
            }
        }
    }
}