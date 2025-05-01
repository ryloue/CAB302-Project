package com.example.cab302finalproj.model;

public class CurrentUser {
    static int currentUserId = -1;

    public static void setCurrentUserId(int userId){
        currentUserId = userId;
    }
    public static int getCurrentUserId(){
        return currentUserId;
    }
    public static void clearID(){
        currentUserId = -1;
    }

}
