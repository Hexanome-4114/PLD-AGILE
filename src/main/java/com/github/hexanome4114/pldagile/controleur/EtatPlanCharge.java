package com.github.hexanome4114.pldagile.controleur;


public final class EtatPlanCharge implements Etat {

    @Override
    public void ajouterLivraison(final Controleur c) {
        c.getSauvegarderLivraisonsBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(false);
        c.setEtatCourant(c.getEtatLivraison());
    }

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
                "Pour ajouter une livraison :"
                        + "\nséléctionnez un livreur, une fenêtre "
                        + "horaire et une adresse en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }
}
