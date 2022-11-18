package com.github.hexanome4114.pldagile.controleur;


/**
 * Etat de l'application lorsque l'on a calculé les tournées.
 * Il est seulement possible de revenir à l'état plan chargé
 * si l'on recharge un nouveau plan.
 */
public final class EtatTournee implements Etat {

    /**
     * Passage de l'état tournée à l'état plan chargé
     * lorsque l'on charge un nouveau plan.
     * @param c le controleur qui change d'état
     */
    @Override
    public void chargerPlan(final Controleur c) {
        c.reinitialiserLivraisons();
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxLivreur().setValue(null);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setValue(null);
        c.getComboBoxAdresse().setValue(null);
        c.getTableauLivraison().getItems().clear();
        c.getComboBoxPlacementLivraison().setDisable(true);
        c.getTableauLivraison().getSelectionModel().clearSelection();
        c.getAjouterLivraisonBouton().setDisable(true);
        c.getSupprimerLivraisonBouton().setDisable(true);
        c.getSauvegarderLivraisonsBouton().setDisable(true);
        c.getChargerLivraisonBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(true);
        c.getAfficherPointsCheckBox().setSelected(false);
        c.getListeDeCommandes().reinitialiser();
        c.getAnnulerBouton().setDisable(true);
        c.getGenererFeuillesDeRouteBouton().setDisable(true);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison, veuillez sélectionner un livreur,"
                        + " une fenêtre horaire et une adresse de livraison"
                        + " en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }
}
