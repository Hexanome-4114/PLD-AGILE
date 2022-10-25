package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public class Livraison {

    private int numero;
    private FenetreDeLivraison fenetreDeLivraison;
    private Livreur livreur;
    private Intersection adresse;

    public Livraison(int numero, FenetreDeLivraison fenetreDeLivraison,Livreur livreur, Intersection adresse) {
        this.numero = numero;
        this.fenetreDeLivraison = fenetreDeLivraison;
        this.livreur = livreur;
        this.adresse = adresse;
    }

    public int getNumero() {
        return numero;
    }

    public FenetreDeLivraison getFenetreDeLivraison() {
        return fenetreDeLivraison;
    }

    public Livreur getLivreur() {
        return livreur;
    }

    public Intersection getAdresse() {
        return adresse;
    }
}
