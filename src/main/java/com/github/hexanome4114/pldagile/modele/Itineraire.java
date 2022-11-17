package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public final class Itineraire {

    private final List<Intersection> intersections;
    private int longueur;

    public Itineraire(final List<Intersection> intersections) {
        this.intersections = intersections;

        // Calcul de la longueur de l'itinéraire
        this.longueur = 0;
        for (int i = 0; i < intersections.size() - 1; ++i) {
            Intersection intersectionSuivante = intersections.get(i + 1);
            this.longueur += intersections.get(i).getIntersections()
                    .get(intersectionSuivante).getKey();
        }
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Itineraire{");
        int nbIntersection = intersections.size();
        String points = "->";
        if (nbIntersection > 2) {
            points = "->" + (nbIntersection - 2) + "->";
            stringBuilder.append(intersections.get(0))
                .append(points)
                .append(intersections.get(nbIntersection - 1));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public int getLongueur() {
        return longueur;
    }
}
