package com.muk.sami.model;

import com.google.firebase.firestore.Exclude;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Trip {

    private @Getter
    String tripId;
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
    @Getter private
    Coordinates startCoordinates;
    @Getter private
    Coordinates destinationCoordinates;
    @Getter private
    String startAddress;
    @Getter private
    String destinationAddress;


    public Trip() {
        // Required empty public constructor
    }

    public Trip(String tripId, Date date, int totalNumberOfSeats, String driver, Coordinates startCoordinates, Coordinates destinationCoordinates, String startAddress, String destinationAddress) {
        this.tripId = tripId;
        this.date = date;
        this.totalNumberOfSeats = totalNumberOfSeats;
        numberOfBookedSeats = 0;
        this.driver = driver;
        this.startCoordinates = startCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
    }

    public boolean tripIsFull() {
        return numberOfBookedSeats == totalNumberOfSeats;
    }

    /**
     * Checks if there are available seats and adds a user to the passenger list if a seat is available
     *
     * @param uid ID of the user to add to the passenger list
     * @return if the user was added successfully
     */

    public boolean addPassenger(String uid) {
        if (!tripIsFull()) {
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

    /**
     * @return "yyyy-MM-dd" formatted string from the Date object in the trip
     */
    @Exclude
    public String getDateString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }

    /**
     * @return "HH:mm" formatted string from the Date object in the trip
     */
    @Exclude
    public String getTimeString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        return simpleDateFormatDate.format(date);
    }

    /**
     * @return a set of the passenger's UIDs
     */
    @Exclude
    public Set<String> getPassengerUids() {
        return passengers.keySet();
    }

    /*
    @Override
    public int compare(Trip o1, Trip o2) {
        return o1.getDate().compareTo(o2.getDate());
    }*/
}

