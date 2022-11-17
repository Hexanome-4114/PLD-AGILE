package com.github.hexanome4114.pldagile.controleur;

public final class EtatInitial implements Etat {

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
