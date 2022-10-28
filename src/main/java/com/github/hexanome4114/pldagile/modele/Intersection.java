package com.github.hexanome4114.pldagile.modele;

import com.gluonhq.maps.MapPoint;

public final class Intersection extends MapPoint {

    private String id;
    public Intersection(String id, final double latitude, final double longitude) {
        super(latitude, longitude);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Intersection{latitude=%f, longitude=%f}",
                getLatitude(), getLongitude());
    }
}
