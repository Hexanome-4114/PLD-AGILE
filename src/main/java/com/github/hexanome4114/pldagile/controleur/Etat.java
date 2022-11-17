package com.github.hexanome4114.pldagile.controleur;


public interface Etat {
    default void chargerPlan(final Controleur c) { }
    default void ajouterLivraison(final Controleur c) { }
    default void chargerLivraison(final Controleur c) { }
    default void calculerTournee(final Controleur c) { }
}
