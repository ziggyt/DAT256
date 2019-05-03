package com.muk.sami.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    Map<String, Boolean> passengers = new HashMap<>();
    private @Getter
    String driver;

    public Trip() {
        // Required empty public constructor
    }


    public Trip(String tripId, String from, String to, Date date, int numberOfBookedSeats, int totalNumberOfSeats, String driver) {
        this.tripId = tripId;
        this.from = from;
        this.to = to;
        this.date = date;
        this.numberOfBookedSeats = numberOfBookedSeats;
        this.totalNumberOfSeats = totalNumberOfSeats;
        this.driver = driver;
    }

    private boolean fullTrip() {
        return numberOfBookedSeats == totalNumberOfSeats;
    }

    /**
     * Checks if there are available seats and adds a user to the passenger list if a seat is available
     *
     * @param uid ID of the user to add to the passenger list
     * @return if the user was added successfully
     */

    public boolean addPassenger(String uid) {
        if (!fullTrip()) {
            numberOfBookedSeats++;
            passengers.put(uid, true);
            return true;
        }
        return false;
    }

    /**
     * Checks if the user has a seat in the current trip and removes it if it has
     *
     * @param uid ID of the user to remove from the passenger list
     * @return if the user was removed successfully
     */

    public boolean removePassenger(String uid) {
        if (userInTrip(uid)) {
            numberOfBookedSeats--;
            passengers.remove(uid);
            return true;
        }
        return false;
    }

    /**
     * @param uid ID of the user to find in the passenger list
     * @return if the user is a passenger in this trip
     */
    public boolean userInTrip(String uid) {
        return passengers.keySet().contains(uid);
    }

    public String getDateString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }

    public String getTimeString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }

    /**
     * @return a set of the passenger's UIDs
     */
    public Set<String> getPassengerUids() {
        return passengers.keySet();
    }

    /*
    @Override
    public int compare(Trip o1, Trip o2) {
        return o1.getDate().compareTo(o2.getDate());
    }*/
}

