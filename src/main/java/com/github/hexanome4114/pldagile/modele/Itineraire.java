package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public final class Itineraire {

    private List<Intersection> intersections;

    public Itineraire(List<Intersection> intersections) {
        this.intersections = intersections;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }
}
