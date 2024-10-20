package com.example.asdfadsf;

public class User {
    public String address;
    public String id;
    public String password;
    public String name;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String address, String id, String password, String name, String phoneNumber) {
        this.address = address;
        this.id = id;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
