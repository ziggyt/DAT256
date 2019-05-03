package com.muk.sami.model;

import java.util.ArrayList;
import java.util.List;
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
    List<Integer> driverRating = new ArrayList<>();

    public User() {
        // Required empty public constructor
    }

    public User(String email, String displayName, String phoneNumber) {
        this.email = email;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        // this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    public void addRating(int rating){
        driverRating.add(rating);
    }


    public double getDriverAverageRating() {
        if (driverRating.size()== 0) {
            return 0;
        }

        int total = 0;
        int nRatings = driverRating.size();

        for (int rating : driverRating) {
            total += rating;
        }

        int averageRatingQuotient = total / nRatings;
        double averageRatingRemainder = total % nRatings;

        return averageRatingQuotient + averageRatingRemainder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, displayName, phoneNumber);
    }
}
