package com.example.cab302finalproj.database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class sqLiteAccountDAO implements IAccountDAO {
    private Connection connection;

    public sqLiteAccountDAO() {
        connection = sqLiteConnection.getInstance();
        createTable();
    }

    private void createTable(){
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAccount(Account account) {

    }

    @Override
    public void updateAccount(Account account) {

    }

    @Override
    public void deleteAccount(Account account) {

    }

    @Override
    public Account getAccount(int id) {
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        return List.of();
    }
}
