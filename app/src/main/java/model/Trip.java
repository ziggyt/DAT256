package model;

import lombok.Getter;

public class Trip {

    @Getter private String location;
    @Getter private String destination;

    public Trip(String location, String destination) {
        this.location = location;
        this.destination = destination;
    }
}
