package com.muk.sami.model;

import lombok.Getter;

public class Trip {

    private @Getter String tripId;
    private @Getter String from;
    private @Getter String to;
    private @Getter String date;
    private @Getter String time;
    private @Getter String seats;

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


}
