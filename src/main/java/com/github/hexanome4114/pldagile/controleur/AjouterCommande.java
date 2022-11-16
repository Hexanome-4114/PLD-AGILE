package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.modele.Livraison;

public final class AjouterCommande implements Commande {

    private final Controleur controleur;
    private final Livraison livraison;

    /**
     * Créer la commande qui ajoute la livraison l au controleur c.
     * @param c Le controleur c
     * @param l La livraison l qui ajouté au controleur c
     */
    public AjouterCommande(final Controleur c, final Livraison l) {
        this.controleur = c;
        this.livraison = l;
    }

    @Override
    public void faireCommande() {
        this.controleur.ajouterLivraison(this.livraison);
    }

    @Override
    public void annulerCommande() {
        this.controleur.supprimerLivraison(this.livraison);
    }
}
