package com.github.hexanome4114.pldagile.modele;

public final class Segment {

    private final String nom;

    /** Longueur du segment en centimètres. */
    private final int longueur;
    private final Intersection debut;
    private final Intersection fin;

    public Segment(final String nom, final int longueur,
                   final Intersection debut, final Intersection fin) {
        this.nom = nom;
        this.longueur = longueur;
        this.debut = debut;
        this.fin = fin;
    }

    public String getNom() {
        return nom;
    }

    public int getLongueur() {
        return longueur;
    }

    public Intersection getDebut() {
        return debut;
    }

    public Intersection getFin() {
        return fin;
    }
}
