package com.github.hexanome4114.pldagile.algorithme.dijkstra;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graphe {
    private Set<Sommet> sommets = new HashSet<>();

    public void ajouterSommet(Sommet sommetA) {
        sommets.add(sommetA);
    }

    public static Graphe calculerCheminplusCourtDepuisSource(Graphe graphe, Sommet source) {
        source.setDistance(0);

        Set<Sommet> sommetsRelaches = new HashSet<>();
        Set<Sommet> sommetsNonRelaches = new HashSet<>();

        sommetsNonRelaches.add(source);

        while (sommetsNonRelaches.size() != 0) {
            Sommet sommetCourant = getSommetLePlusProche(sommetsNonRelaches);
            sommetsNonRelaches.remove(sommetCourant);
            for (Map.Entry< Sommet, Integer> adjacencyPair:
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

    private static Sommet getSommetLePlusProche(Set < Sommet > sommetsNonvisites) {
        Sommet SommetLePlusProche = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Sommet sommet: sommetsNonvisites) {
            int distanceSommet = sommet.getDistance();
            if (distanceSommet < lowestDistance) {
                lowestDistance = distanceSommet;
                SommetLePlusProche = sommet;
            }
        }
        return SommetLePlusProche;
    }

    private static void calculDistanceMinimum(Sommet evaluationNode,
                                                 Integer coutArc, Sommet sommetSource) {
        Integer sourceDistance = sommetSource.getDistance();
        if (sourceDistance + coutArc < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + coutArc);
            LinkedList<Sommet> cheminPlusCourt = new LinkedList<>(sommetSource.getCheminPlusCourt());
            cheminPlusCourt.add(sommetSource);
            evaluationNode.setCheminPlusCourt(cheminPlusCourt);
        }
    }
}
