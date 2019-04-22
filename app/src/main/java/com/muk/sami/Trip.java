package com.muk.sami;

public class Trip {
    String tripId;
    String from;
    String to;
    String date;
    String time;
    String seats;

    public Trip(){
        // Required empty public constructor
    }

    public Trip(String tripId, String from, String to, String date, String time, String seats) {
        this.tripId = tripId;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.seats = seats;
    }

    public String getTripId() {
        return tripId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSeats() {
        return seats;
    }
}
