package com.github.hexanome4114.pldagile.controleur;


import com.github.hexanome4114.pldagile.utilitaire.CalquePlan;

public final class EtatLivraison implements Etat {

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
        c.setCurrentState(c.getEtatPlanCharge());
    }
}
