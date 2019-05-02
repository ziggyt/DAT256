package com.muk.sami.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

public class Trip {

    private @Getter
    String tripId;
    private @Getter
    String from;
    private @Getter
    String to;
    private @Getter
    Date date;
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


    public Trip(String tripId, String from, String to, Date date, int numberOfBookedSeats, int totalNumberOfSeats, User driver) {
        this.tripId = tripId;
        this.from = from;
        this.to = to;
        this.date = date;
        this.numberOfBookedSeats = numberOfBookedSeats;
        this.totalNumberOfSeats = totalNumberOfSeats;
        this.driver = driver;
    }

    public boolean fullTrip() {
        return numberOfBookedSeats == totalNumberOfSeats;
    }

    /**
     * Checks if there are available seats and adds a user to the passenger list if a seat is available
     *
     * @param user user to add to the passenger list
     * @return if the user was added successfully
     */

    public boolean addPassenger(User user) {
        if (!fullTrip()) {
            numberOfBookedSeats++;
            passengers.add(user);
            return true;
        }
        return false;
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

    public String getDateString(){
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }

    public String getTimeString(){
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("HH-mm", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }
}

