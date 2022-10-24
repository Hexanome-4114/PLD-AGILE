package com.github.hexanome4114.pldagile.modele;

public class Livreur {
    private String nom;
    private Tournee tournee;
    private float vitesseMoyenne;

    public Livreur(String nom, Tournee tournee, float vitesseMoyenne) {
        this.nom = nom;
        this.tournee = tournee;
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public String getNom() {
        return nom;
    }

    public Tournee getTournee() {
        return tournee;
    }

    public float getVitesseMoyenne() {
        return vitesseMoyenne;
    }
}
