package com.github.hexanome4114.pldagile.controleur;

public final class EtatTournee implements Etat {

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
}
