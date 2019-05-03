package com.muk.sami.model;

import java.util.Objects;

import lombok.Getter;

public class User {

    private @Getter
    String email;
    private @Getter
    String displayName;
    private @Getter
    String phoneNumber;
    //private @Getter String address;
    private @Getter
    int driverRating;

    public User() {
        // Required empty public constructor
    }

    public User(String email, String displayName, String phoneNumber, String address) {
        this.email = email;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.driverRating = 3;
        // this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, displayName, phoneNumber);
    }
}
