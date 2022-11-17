package com.github.hexanome4114.pldagile.controleur;


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
        c.getListeDeCommandes().reinitialiser();
        c.getAnnulerBouton().setDisable(true);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison, veuillez séléctionner un livreur,"
                        + " une fenêtre horaire et une adresse de livraison"
                        + " en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }

    @Override
    public void chargerLivraison(final Controleur c) {
        c.getSauvegarderLivraisonsBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(false);
        c.getListeDeCommandes().reinitialiser();
        c.getAnnulerBouton().setDisable(true);
        c.setEtatCourant(c.getEtatLivraison());
    }

    @Override
    public void calculerTournee(final Controleur c) {
        c.getComboBoxLivreur().getSelectionModel().clearSelection();
        c.getComboBoxLivreur().getItems().clear();
        c.getComboBoxFenetreDeLivraison().getSelectionModel().clearSelection();
        c.getComboBoxFenetreDeLivraison().getItems().clear();
        c.getComboBoxLivreur().setDisable(true);
        c.getComboBoxFenetreDeLivraison().setDisable(true);
        c.getAjouterLivraisonBouton().setDisable(true);
        c.getCalculerTourneeBouton().setDisable(true);
        c.getChargerLivraisonBouton().setDisable(true);
        c.setEtatCourant(c.getEtatTournee());
    }
}
