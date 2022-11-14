package com.github.hexanome4114.pldagile.controleur;

public final class EtatInitial implements Etat {

    @Override
    public void chargerPlan(final Controleur c) {
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getChargerLivraisonBouton().setDisable(false);
        c.getAfficherPointsCheckBox().setDisable(false);
        c.setCurrentState(c.getEtatPlanCharge());
    }
}
