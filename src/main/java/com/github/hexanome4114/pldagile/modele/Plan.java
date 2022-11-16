package com.github.hexanome4114.pldagile.modele;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.utilitaire.TourneeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Calcule l'itinéraire (plus court chemin) entre la source et toutes les autres intersections
     * @param source intersection source
     * @return clef : intersection, valeur : itinéraire = itineraire entre la source et la clef
     */
    public Map<Intersection, Itineraire> getItineraire(final Intersection source) {
        Map<Intersection, Itineraire> result = new HashMap<>();
        Graphe graphePlan = TourneeHelper.creerGrapheDijkstra(this);

        Graphe.calculerCheminplusCourtDepuisSource(graphePlan, graphePlan.getSommets()
                .get(source.getId()));
        for (Sommet sommet : graphePlan.getSommets().values()) {
            List<Intersection> intersectionsItineraire = new ArrayList<>(sommet.getCheminPlusCourt().size());
            List<Sommet> plusCourtCheminCourant = sommet.getCheminPlusCourt();

            // S'il n'y a pas de chemin plus court entre les 2, on n'ajoute pas
            if (!plusCourtCheminCourant.isEmpty()) {
                for (Sommet sommet1 : plusCourtCheminCourant) {
                    Intersection intersectionDuPcc = intersections.get(sommet1.getNom());
                    intersectionsItineraire.add(intersectionDuPcc);
                }
                intersectionsItineraire.add(intersections.get(sommet.getNom())); // On doit ajouter l'intersection destination
            }
            Itineraire itineraire = new Itineraire(intersectionsItineraire);
            result.put(this.intersections.get(sommet.getNom()), itineraire);
        }
        return result;
    }
}
