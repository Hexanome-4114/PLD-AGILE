package com.github.hexanome4114.pldagile.algorithme.dijkstra;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graphe {
    private Map<String, Sommet> sommets = new LinkedHashMap<>();

    public Map<String, Sommet> getSommets() {
        return sommets;
    }

    public void ajouterSommet(final Sommet sommetA) {
        sommets.put(sommetA.getNom(), sommetA);
    }


    public void reinitialiserSommetsGraphe() {
        for (Sommet sommet : sommets.values()) {
            sommet.setDistance(Integer.MAX_VALUE);
            sommet.setCheminPlusCourt(new LinkedList<>());
        }
    }

    public static Graphe calculerCheminplusCourtDepuisSource(Graphe graphe, Sommet source) {
        source.setDistance(0);

        Set<Sommet> sommetsRelaches = new HashSet<>();
        Set<Sommet> sommetsNonRelaches = new HashSet<>();

        sommetsNonRelaches.add(source);

        while (sommetsNonRelaches.size() != 0) {
            Sommet sommetCourant = getSommetLePlusProche(sommetsNonRelaches);
            sommetsNonRelaches.remove(sommetCourant);
            for (Map.Entry<Sommet, Integer> adjacencyPair :
                    sommetCourant.getSommetsAdjacents().entrySet()) {
                Sommet sommetAdjacent = adjacencyPair.getKey();
                Integer coutArc = adjacencyPair.getValue();
                if (!sommetsRelaches.contains(sommetAdjacent)) {
                    calculDistanceMinimum(sommetAdjacent, coutArc, sommetCourant);
                    sommetsNonRelaches.add(sommetAdjacent);
                }
            }
            sommetsRelaches.add(sommetCourant);
        }
        return graphe;
    }

    private static Sommet getSommetLePlusProche(Set<Sommet> sommetsNonvisites) {
        Sommet SommetLePlusProche = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Sommet sommet : sommetsNonvisites) {
            int distanceSommet = sommet.getDistance();
            if (distanceSommet < lowestDistance) {
                lowestDistance = distanceSommet;
                SommetLePlusProche = sommet;
            }
        }
        return SommetLePlusProche;
    }

    private static void calculDistanceMinimum(Sommet sommetEvalue/*un sommet adjacent à la source*/,
                                              Integer coutArc, Sommet sommetSource) {
        Integer sourceDistance = sommetSource.getDistance();
        if (sourceDistance + coutArc < sommetEvalue.getDistance()) {
            sommetEvalue.setDistance(sourceDistance + coutArc);
            LinkedList<Sommet> cheminPlusCourt = new LinkedList<>(sommetSource.getCheminPlusCourt());
            cheminPlusCourt.add(sommetSource);
            sommetEvalue.setCheminPlusCourt(cheminPlusCourt);
        }
    }

    @Override
    public String toString() {
        return "Graphe{" +
                "sommets=" + sommets +
                '}';
    }
}
