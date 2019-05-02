package com.muk.sami.model;

import lombok.Getter;

public class User {

    private @Getter
    String email;
    private @Getter
    String displayName;
    private @Getter
    String phoneNumber;
    //private @Getter String address;

    public User() {
        // Required empty public constructor
    }

    public User(String email, String displayName, String phoneNumber, String address) {
        this.email = email;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        // this.address = address;
    }
}
