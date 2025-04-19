package com.example.cab302finalproj.database;

public class Account {
    private int id;
    private String email;
    private String password;

    public Account (String firstName, String lastName, String email, String phone) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String phone) {
        this.password = password;
    }
}
