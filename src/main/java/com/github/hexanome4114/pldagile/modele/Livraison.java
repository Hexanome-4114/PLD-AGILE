package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public class Livraison {
    private List<Integer> fenetreLivraison;
    private Livreur livreur;

    public Livraison(List<Integer> fenetreLivraison, Livreur livreur) {
        this.fenetreLivraison = fenetreLivraison;
        this.livreur = livreur;
    }

    public List<Integer> getFenetreLivraison() {
        return this.fenetreLivraison;
    }

    public Livreur getLivreur() {
        return this.livreur;
    }
}
