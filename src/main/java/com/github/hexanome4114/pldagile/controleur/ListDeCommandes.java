package com.github.hexanome4114.pldagile.controleur;

import javafx.beans.value.ObservableNumberValue;

import java.util.LinkedList;

public final class ListDeCommandes {
    private final LinkedList<Commande> listeDeCommande;
    private int indexCourant;

    public ListDeCommandes() {
        this.indexCourant = -1;
        this.listeDeCommande = new LinkedList<Commande>();
    }

    /**
     * Ajoute la commande c à l'instance.
     * @param c La commande à ajouter
     */
    public void add(final Commande c) {
        int i = this.indexCourant + 1;
        while (i < this.listeDeCommande.size()) {
            this.listeDeCommande.remove(i);
        }
        this.indexCourant++;
        this.listeDeCommande.add(indexCourant, c);
        c.faireCommande();
    }

    /**
     * Supprime temporairement la dernière commande ajouté.
     */
    public void undo() {
        if (this.indexCourant >= 0) {
            Commande commande = listeDeCommande.get(indexCourant);
            this.indexCourant--;
            commande.annulerCommande();
        }
    }

    /**
     * Supprime définitivement les commandes de la liste.
     */
    public void reset() {
        this.indexCourant = -1;
        this.listeDeCommande.clear();
    }

    public int getIndexCourant() {
        return this.indexCourant;
    }
}
