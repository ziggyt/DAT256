package com.muk.sami.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Trip {

    private @Getter
    String tripId;
    private @Getter
    String from;
    private @Getter
    String to;
    private @Getter
    String date;
    private @Getter
    String time;
    private @Getter
    int numberOfBookedSeats;
    private @Getter
    int totalNumberOfSeats;

    private @Getter
    List<User> passengers = new ArrayList<>();
    private @Getter
    User driver;

    public Trip() {
        // Required empty public constructor
    }

    public Trip(String tripId, String from, String to, String date, String time, int seats) {
        this.tripId = tripId;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.totalNumberOfSeats = seats;
        this.numberOfBookedSeats = 0;
    }

    public boolean FullTrip() {
        if(numberOfBookedSeats < totalNumberOfSeats) {
            return false;
        } else {
            return true;
        }
    }

    public void addPassenger() {
        numberOfBookedSeats++;
    }

    /**
     * Checks if the user has a seat in the current trip and removes it if it has
     * @param user user to remove from passenger list
     * @return if the user was removed successfully
     */

    public boolean removePassenger(User user) {
        if (userInTrip(user)) {
            numberOfBookedSeats--;
            passengers.remove(user);
            return true;
        }
        return false;
    }

    /**
     * @param user user to find in passenger list
     * @return if the user is a passenger in this trip
     */

    public boolean userInTrip(User user) {
        for (User u : passengers) {
            if (u.equals(user)) {
                return true;
            }
        }
        return false;
    }
}
