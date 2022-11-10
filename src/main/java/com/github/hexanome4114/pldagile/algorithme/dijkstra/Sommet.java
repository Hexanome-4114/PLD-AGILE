package com.github.hexanome4114.pldagile.algorithme.dijkstra;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sommet {

    private String nom;
    private List<Sommet> cheminPlusCourt = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;
    Map<Sommet, Integer> sommetsAdjacents = new HashMap<>();

    public Sommet (String nom) {
        this.nom = nom;
    }

    public void addDestination(Sommet destination, int distance) {
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

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setCheminPlusCourt(List<Sommet> cheminPlusCourt) {
        this.cheminPlusCourt = cheminPlusCourt;
    }

    public String toString (){
        String result = "";
        for(Map.Entry<Sommet,Integer> sommetsAdjacents : this.sommetsAdjacents.entrySet()) {
            result += "{" + sommetsAdjacents.getKey().getNom() + ";" + sommetsAdjacents.getValue() + "}";
        }
        return result;
    }
}
