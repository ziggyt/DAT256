package com.muk.sami.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class User {

    private @Getter
    String email;

    private @Getter
    String displayName;

    private @Getter
    String phoneNumber;

    //private @Getter String address;

    //An average of the ratings received (1-5)
    private @Getter
    double driverRating;

    //The number of ratings received
    private @Getter
    int numberOfRatings;

    private @Getter
    String photoURL;

    private @Getter @Setter
    BankCard bankCard;

    private @Getter @Setter
    int savedCarbon;

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

    /**
     * The method takes the average rating and multiplies by the total number of ratings to get the
     * sum of all ratings. Then adds the new rating and divides by the new number of ratings.
     *
     * @param rating the rating to be added
     */
    public void addRating(double rating){

        //Calculate the total number of stars
        double total = driverRating * numberOfRatings;

        //Add the new rating and divide by numberOfRatings to get new average
        double newTotal = total + rating; numberOfRatings++;
        double newAverage = newTotal/numberOfRatings;

        driverRating = newAverage;

    }

    /*public void addRating(int rating){
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
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(email, displayName, phoneNumber);
    }
}
