package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public final class Plan {

    private final List<Segment> segments;

    private final Intersection entrepot;

    public Plan(final List<Segment> segments, final Intersection entrepot) {
        this.segments = segments;
        this.entrepot = entrepot;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public Intersection getEntrepot() {
        return entrepot;
    }
}
