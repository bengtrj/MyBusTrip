package com.mybustrip.model;

/**
 * Created by bengthammarlund on 12/05/16.
 */
public class Location {

    private double latitude;

    private double longitude;

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

    public double getDistanceInMeters(final Location otherLocation) {
        return distance(
                latitude, longitude,
                otherLocation.latitude, otherLocation.longitude
        );
    }

    public double getDistanceInMeters(final android.location.Location latLng) {
        return distance(
                latitude, longitude,
                latLng.getLatitude(), latLng.getLongitude()
        );
    }

    /**
     * Calculates the distance between two locations in Km or Miles
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371.0; // 3959 for miles, 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1); //(lat2 - lat1) / 180d * 3.141592653589793
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist;
    }
}
