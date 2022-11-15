package com.github.hexanome4114.pldagile.algorithme.dijkstra;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Sommet {

    private String nom;
    private List<Sommet> cheminPlusCourt = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;
    private Map<Sommet, Integer> sommetsAdjacents = new HashMap<>();

    public Sommet(final String nom) {
        this.nom = nom;
    }

    public void addDestination(final Sommet destination, final int distance) {
        sommetsAdjacents.put(destination, distance);
    }

    public String getNom() {
        return nom;
    }

    public List<Sommet> getCheminPlusCourt() {
        return cheminPlusCourt;
    }

    public Integer getDistance() {
        return distance;
    }

    public Map<Sommet, Integer> getSommetsAdjacents() {
        return sommetsAdjacents;
    }

    public void setDistance(final Integer distance) {
        this.distance = distance;
    }

    public void setCheminPlusCourt(final List<Sommet> cheminPlusCourt) {
        this.cheminPlusCourt = cheminPlusCourt;
    }

    @Override
    public String toString() {
        return "Sommet<" + getNom() + ">";
    }
}
