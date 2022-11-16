package com.github.hexanome4114.pldagile.modele;

import java.util.HashMap;

public final class Plan {

    private final HashMap<String, Intersection> intersections;

    private final Intersection entrepot;

    public Plan(final HashMap<String, Intersection> intersections,
                final Intersection entrepot) {
        this.intersections = intersections;
        this.entrepot = entrepot;
    }

    public HashMap<String, Intersection> getIntersections() {
        return intersections;
    }

    public Intersection getEntrepot() {
        return entrepot;
    }
}
