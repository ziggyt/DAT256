package com.muk.sami.model;

import com.google.firebase.firestore.Exclude;

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
    List<Integer> samiRating = new ArrayList<>();

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
        samiRating.add(rating);
    }


    @Exclude
    public double getAverageSamiRating() {
        if (samiRating.size()== 0) {
            return 0;
        }

        int total = 0;
        int nRatings = samiRating.size();

        for (int rating : samiRating) {
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
