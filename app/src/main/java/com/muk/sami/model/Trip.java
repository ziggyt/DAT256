package com.muk.sami.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Trip {

    @Getter private
    String tripId;
    @Getter private
    Date date;
    @Getter private
    int numberOfBookedSeats;
    @Getter private
    int totalNumberOfSeats;
    @Getter private
    List<String> passengers = new ArrayList<>();
    @Getter private
    List<String> passengerStatus = new ArrayList<>();
    @Getter private
    String driver;
    @Getter private
    Coordinates startCoordinates;
    @Getter private
    Coordinates destinationCoordinates;
    @Getter private
    String startAddress;
    @Getter private
    String destinationAddress;
    @Getter private
    boolean tripStarted;


    public Trip() {
        // Required empty public constructor
    }

    public Trip(String tripId, Date date, int totalNumberOfSeats, String driver, Coordinates startCoordinates, Coordinates destinationCoordinates, String startAddress, String destinationAddress) {
        this.tripId = tripId;
        this.date = date;
        this.totalNumberOfSeats = totalNumberOfSeats;
        this.numberOfBookedSeats = 0;
        this.driver = driver;
        this.startCoordinates = startCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
    }

    public void startTrip(){
        tripStarted = true;
    }

    public void finishTripPassenger(String passenger){
        int passengerIndex = passengers.indexOf(passenger);
        passengerStatus.set( passengerIndex, "Finished trip");
    }

    public boolean tripIsFinished(){
        if( passengers.isEmpty()){
            return false;
        }
        for(String passenger : passengerStatus) {
            if( !passenger.equals("Finished trip")){
                return false;
            }
        }
        return true;
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
            passengers.add(uid);
            passengerStatus.add("Joined");

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
            passengerStatus.remove( passengers.indexOf(uid) );
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
        return passengers.contains(uid);
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

    public double getDistanceBetweenStartAndDestination(){
        return startCoordinates.kilometersBetweenCoordinates(destinationCoordinates);
    }

    public double getDistanceBetweenStartAndCustomCoordinates(Coordinates c1){
        return startCoordinates.kilometersBetweenCoordinates(c1);
    }

    public int getSeatPrice() {
        double distanceInKm = getDistanceBetweenStartAndDestination()/1000.0;
        return (int)(distanceInKm*2)/(totalNumberOfSeats+1);
    }
}

