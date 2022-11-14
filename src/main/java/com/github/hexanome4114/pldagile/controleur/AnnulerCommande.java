package com.github.hexanome4114.pldagile.controleur;

public final class AnnulerCommande implements Commande {

    private final Commande commande;
    /**
     * Créer la commande inverse de la commande c.
     * @param c La commande à annuler
     */
    public AnnulerCommande(final Commande c) {
        this.commande = c;
    }

    @Override
    public void faireCommande() {
        this.commande.annulerCommande();
    }

    @Override
    public void annulerCommande() {
        this.commande.faireCommande();
    }
}
