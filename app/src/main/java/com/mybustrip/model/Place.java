package com.mybustrip.model;

import android.location.Location;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonDeserialize(using = PlaceDeserializer.class)
public class Place {

    private Long id;

    private String name;

    private String icon;

    private double latitude;

    private double longitude;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance(final Place otherPlace) {
        return distance(
                latitude, longitude,
                otherPlace.latitude, otherPlace.longitude
        );
    }

    public double getDistance(final Location latLng) {
        return distance(
                latitude, longitude,
                latLng.getLatitude(), latLng.getLongitude()
        );
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371.0; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
}
