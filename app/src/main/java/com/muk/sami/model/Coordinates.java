package com.muk.sami.model;

import lombok.Getter;

public class Coordinates {

    @Getter
    private double lat;
    @Getter
    private double lon;

    public Coordinates() {
    }

    public Coordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
