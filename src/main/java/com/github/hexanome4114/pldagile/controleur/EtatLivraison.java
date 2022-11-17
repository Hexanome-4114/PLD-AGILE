package com.github.hexanome4114.pldagile.controleur;


/**
 * Etat de l'application lorsque des livraisons ont été ajoutées.
 * Il est possible de rajouter des livraisons ou en supprimer,
 * l'état livraison est conservé même si il n'y a aucune livraison.
 * Il est possible de passer à l'état tournée si l'on clique sur
 * le bouton calculer les tournées, il est aussi possible de passer
 * à l'état plan chargé si l'on recharge un plan, les livraisons sont
 * alors réinitialiser. Si l'on charge des livraisons depuis un fichier,
 * l'état est conservé mais les livraisons sont réinitialiser
 */
public final class EtatLivraison implements Etat {

    /**
     * Passage de l'état livraison à l'état plan chargé
     * lorsque l'on charge un nouveau plan.
     * @param c le controleur qui change d'état
     */
    @Override
    public void chargerPlan(final Controleur c) {
        c.reinitialiserLivraisons();
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
        c.getTableauLivraison().refresh();
        c.getComboBoxLivreur().setValue(null);
        c.getComboBoxLivreur().setDisable(false);
        c.getComboBoxFenetreDeLivraison().setValue(null);
        c.getComboBoxFenetreDeLivraison().setDisable(false);
        c.getAjouterLivraisonBouton().setDisable(true);
        c.getCalculerTourneeBouton().setDisable(true);
        c.getChargerLivraisonBouton().setDisable(true);
        c.getGenererFeuillesDeRouteBouton().setDisable(false);
        c.getInstructionLabel().setText(
                "Pour ajouter une livraison, veuillez sélectionner un livreur,"
                        + " une fenêtre horaire, une adresse de livraison"
                        + " en cliquant sur la carte et la place de la"
                        + " nouvelle livraison dans la tournée."
        );
        c.setEtatCourant(c.getEtatTournee());
    }
}
