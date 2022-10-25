package com.github.hexanome4114.pldagile.modele;

public class Segment {
    private String nom;
    private Integer longueur;
    private Intersection intersectionDebut;
    private Intersection intersectionFin;

    public Segment(String nom, Integer longueur, Intersection intersectionDebut, Intersection intersectionFin) {
        this.nom = nom;
        this.longueur = longueur;
        this.intersectionDebut = intersectionDebut;
        this.intersectionFin = intersectionFin;
    }

    public String getNom() {
        return nom;
    }

    public Integer getLongueur() {
        return longueur;
    }

    public Intersection getIntersectionDebut() {
        return intersectionDebut;
    }

    public Intersection getIntersectionFin() {
        return intersectionFin;
    }
}
