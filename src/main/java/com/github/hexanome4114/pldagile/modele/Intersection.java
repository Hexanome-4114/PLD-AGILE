package com.github.hexanome4114.pldagile.modele;

import com.gluonhq.maps.MapPoint;

public final class Intersection extends MapPoint {

    public Intersection(final double latitude, final double longitude) {
        super(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("Intersection{latitude=%f, longitude=%f}",
                getLatitude(), getLongitude());
    }
}
