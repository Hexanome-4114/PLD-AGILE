package com.github.hexanome4114.pldagile.controleur;


public final class EtatLivraison implements Etat {

    /**
     * Passage de l'état livraison à l'état plan chargé
     * lorsque l'on charge un nouveau plan.
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
                "Pour ajouter une livraison, veuillez séléctionner un livreur,"
                        + " une fenêtre horaire et une adresse de livraison"
                        + " en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }

    /**
     * Réinitialise l'état livraison
     * lorsque l'on charge de nouvelles livraisons.
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

    /**
     * Passage de l'état livraison à l'état tournée
     * lorsque l'on calcule les tournées.
     * @param c le controleur qui change d'état
     */
    @Override
    public void calculerTournee(final Controleur c) {
        c.getComboBoxLivreur().setValue(null);
        c.getComboBoxLivreur().setDisable(true);
        c.getComboBoxFenetreDeLivraison().setValue(null);
        c.getComboBoxFenetreDeLivraison().setDisable(true);
        c.getAjouterLivraisonBouton().setDisable(true);
        c.getCalculerTourneeBouton().setDisable(true);
        c.getChargerLivraisonBouton().setDisable(true);
        c.getInstructionLabel().setText(
                "Comment ajouter nouvelle livraison"
        );
        c.setEtatCourant(c.getEtatTournee());
    }
}
