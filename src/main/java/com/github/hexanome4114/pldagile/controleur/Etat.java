package com.github.hexanome4114.pldagile.controleur;


public interface Etat {
    default void chargerPlan(final Controleur c) { }
    default void ajouterLivraison(final Controleur c) { }
}
