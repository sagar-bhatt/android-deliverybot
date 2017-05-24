package com.example.sagar.deliverybot;

/**
 * Created by Sagar on 5/14/2017.
 */

public class Driver {
    public String username;
    public String email;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String email) {
        this.email = email;
    }

    public Driver(String username, String email) {
        this.username= username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
