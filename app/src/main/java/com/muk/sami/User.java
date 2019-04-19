package com.muk.sami;

public class User {

    private String email;
    private String displayName;
    private String phoneNumber;
    private String adress;

    public User() {
        // Required empty public constructor
    }

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAdress() {
        return adress;
    }
}
