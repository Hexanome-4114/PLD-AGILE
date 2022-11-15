package com.github.hexanome4114.pldagile.controleur;

public final class EtatInitial implements Etat {

    @Override
    public void chargerPlan(final Controleur c) {
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getChargerLivraisonBouton().setDisable(false);
        c.getAfficherPointsCheckBox().setDisable(false);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison :"
                        + "\nséléctionnez un livreur, une fenêtre "
                        + "horaire et une adresse en cliquant sur la carte."
        );
        c.setEtatCourant(c.getEtatPlanCharge());
    }
}
