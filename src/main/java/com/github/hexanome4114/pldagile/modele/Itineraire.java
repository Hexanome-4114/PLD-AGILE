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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Itineraire{");
        int nbIntersection = intersections.size();
        String points = "->";
        if(nbIntersection > 2){
            points = "->"+(nbIntersection-2)+"->";
        }
        stringBuilder.append(intersections.get(0))
                .append(points)
                .append(intersections.get(nbIntersection-1))
                .append('}');
        return stringBuilder.toString();
    }
}
