package com.github.hexanome4114.pldagile.controleur;


/**
 * Etat de l'application lorsque qu'aucun plan n'a été chargé,
 * il n'est alors possible que de charger un plan pour passer à
 * l'état plan chargé.
 */
public final class EtatInitial implements Etat {

    /**
     * Passage de l'état initial à l'état plan chargé
     * lorsque l'on charge un plan.
     * @param c le controleur qui change d'état
     */
    @Override
    public void chargerPlan(final Controleur c) {
        c.reinitialiserLivraisons();
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getChargerLivraisonBouton().setDisable(false);
        c.getAfficherPointsCheckBox().setDisable(false);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison, veuillez sélectionner un livreur,"
                        + " une fenêtre horaire et une adresse de livraison"
                        + " en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }
}
