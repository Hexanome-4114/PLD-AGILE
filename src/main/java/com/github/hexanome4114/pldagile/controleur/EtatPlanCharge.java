package com.github.hexanome4114.pldagile.controleur;


/**
 * Etat de l'application lorsque le plan à été chargé.
 * Il est possible de passer à l'état livraison si l'on ajoute
 * des livraisons ou si l'on en charge.
 */
public final class EtatPlanCharge implements Etat {

    /**
     * Réinitialise l'état plan chargé lorsque l'on charge un nouveau plan.
     * @param c le controleur qui change d'état
     */
    @Override
    public void chargerPlan(final Controleur c) {
        c.getComboBoxLivreur().setValue(null);
        c.getComboBoxFenetreDeLivraison().setValue(null);
        c.getComboBoxAdresse().setValue(null);
        c.getTableauLivraison().getItems().clear();
        c.getTableauLivraison().getSelectionModel().clearSelection();
        c.getAjouterLivraisonBouton().setDisable(true);
        c.getSupprimerLivraisonBouton().setDisable(true);
        c.getSauvegarderLivraisonsBouton().setDisable(true);
        c.getCalculerTourneeBouton().setDisable(true);
        c.getAfficherPointsCheckBox().setSelected(false);
        c.getListeDeCommandes().reinitialiser();
        c.getAnnulerBouton().setDisable(true);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison, veuillez sélectionner un livreur,"
                        + " une fenêtre horaire et une adresse de livraison"
                        + " en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }

    /**
     * Passage de l'état plan chargé à l'état livraison
     * lorsque l'on ajoute une livraison.
     * @param c le controleur qui change d'état
     */
    @Override
    public void ajouterLivraison(final Controleur c) {
        c.getSauvegarderLivraisonsBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(false);
        c.setEtatCourant(c.getEtatLivraison());
    }

    /**
     * Passage de l'état plan chargé à l'état livraison
     * lorsque l'on charge des livraisons.
     * @param c le controleur qui change d'état
     */
    @Override
    public void chargerLivraison(final Controleur c) {
        c.getSauvegarderLivraisonsBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(false);
        c.getListeDeCommandes().reinitialiser();
        c.getAnnulerBouton().setDisable(true);
        c.setEtatCourant(c.getEtatLivraison());
    }
}
