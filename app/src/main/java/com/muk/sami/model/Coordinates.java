package com.muk.sami.model;
import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Coordinates implements Serializable {

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


    /**
     * A method to compute the distance in kilometers between two Coordinate objects
     * @param otherLocation the other coordinates to compare this coordinate to
     * @return the distance in kilometers between two coordinates
     */

    double kilometersBetweenCoordinates(Coordinates otherLocation) {

        double lat1 = lat;
        double lon1 = lon;
        double lat2 = otherLocation.lat;
        double lon2 = otherLocation.lon;


        final int radiusOfEarth = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = radiusOfEarth * c * 1000;

        return Math.abs(distance);

    }
}
