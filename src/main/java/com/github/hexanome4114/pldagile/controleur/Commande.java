package com.github.hexanome4114.pldagile.controleur;


public interface Commande {

    /**
     * Execute la commande.
     */
    void faireCommande();

    /**
     * Execute l'inverse de la commande.
     */
    void annulerCommande();
}
