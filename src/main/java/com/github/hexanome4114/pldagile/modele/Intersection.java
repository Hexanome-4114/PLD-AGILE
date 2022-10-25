package com.github.hexanome4114.pldagile.modele;

public class Intersection {
    private double latitude;
    private double longitude;

    public Intersection(double lat, double longi) {
        this.latitude = lat;
        this.longitude = longi;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
