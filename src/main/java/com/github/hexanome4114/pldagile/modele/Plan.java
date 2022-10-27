package com.github.hexanome4114.pldagile.modele;

import java.util.HashMap;
import java.util.List;

public final class Plan {

    private final List<Segment> segments;

    private final HashMap<String, Intersection> intersections;

    private final Intersection entrepot;

    public Plan(final List<Segment> segments, final HashMap<String, Intersection> intersections, final Intersection entrepot) {
        this.segments = segments;
        this.intersections = intersections;
        this.entrepot = entrepot;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public HashMap<String, Intersection> getIntersections() {
        return intersections;
    }

    public Intersection getEntrepot() {
        return entrepot;
    }
}
