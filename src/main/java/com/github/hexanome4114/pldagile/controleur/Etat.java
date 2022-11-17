package com.github.hexanome4114.pldagile.controleur;


public interface Etat {
    /**
     * Passage à l'état plan chargé.
     * @param c le controleur qui change d'état
     */
    default void chargerPlan(final Controleur c) { }
    /**
     * Passage à l'état livraison.
     * @param c le controleur qui change d'état
     */
    default void ajouterLivraison(final Controleur c) { }
    /**
     * Passage à l'état livraison.
     * @param c le controleur qui change d'état
     */
    default void chargerLivraison(final Controleur c) { }
    /**
     * Passage à l'état tournée.
     * @param c le controleur qui change d'état
     */
    default void calculerTournee(final Controleur c) { }
}
