package com.github.hexanome4114.pldagile.controleur;

public final class EtatInitial implements Etat {

    @Override
    public void chargerPlan(final Controleur c) {
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getComboBoxLivreur().valueProperty().
                addListener((options, oldValue, newValue) ->
                        c.getAjouterLivraisonBouton().setDisable(
                        c.getComboBoxLivreur().getValue() == null
                        || c.getComboBoxFenetreDeLivraison().getValue() == null
                        || c.getComboBoxAdresse().getValue() == null));
        c.getComboBoxFenetreDeLivraison().valueProperty().
                addListener((options, oldValue, newValue) ->
                        c.getAjouterLivraisonBouton().setDisable(
                        c.getComboBoxLivreur().getValue() == null
                        || c.getComboBoxFenetreDeLivraison().getValue() == null
                        || c.getComboBoxAdresse().getValue() == null));
        c.getComboBoxAdresse().valueProperty().
                addListener((options, oldValue, newValue) ->
                        c.getAjouterLivraisonBouton().setDisable(
                        c.getComboBoxLivreur().getValue() == null
                        || c.getComboBoxFenetreDeLivraison().getValue() == null
                        || c.getComboBoxAdresse().getValue() == null));
        c.getChargerLivraisonBouton().setDisable(false);
        c.getAfficherPointsCheckBox().setDisable(false);
        c.setCurrentState(c.getEtatPlanCharge());
    }
}
