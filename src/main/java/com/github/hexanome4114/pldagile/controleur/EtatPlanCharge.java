package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.modele.Livraison;
import javafx.collections.ListChangeListener;

public final class EtatPlanCharge implements Etat {

    @Override
    public void ajouterLivraison(final Controleur c) {
        c.getSauvegarderLivraisonsBouton().setDisable(false);
        c.getCalculerTourneeBouton().setDisable(false);
        c.getTableauLivraison().getSelectionModel().selectedItemProperty()
                // bouton cliquable que lorsqu'une livraison est sélectionnée
                .addListener((obs, ancienneSelection, nouvelleSelection)
                        -> c.getSupprimerLivraisonBouton().setDisable(
                        nouvelleSelection == null)
                );
        c.getTableauLivraison().getItems()
                // bouton cliquable que lorsqu'il y a des livraisons
                .addListener((ListChangeListener<Livraison>) (obs)
                        -> {
                    c.getSauvegarderLivraisonsBouton().setDisable(
                            obs.getList().isEmpty());
                    c.getCalculerTourneeBouton().setDisable(
                            obs.getList().isEmpty());
                }
        );
        c.setCurrentState(c.getEtatLivraison());
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
        c.setCurrentState(c.getEtatPlanCharge());
    }
}
