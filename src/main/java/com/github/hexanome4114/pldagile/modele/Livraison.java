package com.github.hexanome4114.pldagile.modele;

@SuppressWarnings("checkstyle:RegexpSingleline")
public final class Livraison {

    private final int numero;
    private final FenetreDeLivraison fenetreDeLivraison;
    private final Livreur livreur;
    private final Intersection adresse;

    public Livraison(final int numero,
                     final FenetreDeLivraison fenetreDeLivraison,
                     final Livreur livreur, final Intersection adresse) {
        this.numero = numero;
        this.fenetreDeLivraison = fenetreDeLivraison;
        this.livreur = livreur;
        this.adresse = adresse;
    }

    public int getNumero() {
        return numero;
    }

    public FenetreDeLivraison getFenetreDeLivraison() {
        return fenetreDeLivraison;
    }

    public Livreur getLivreur() {
        return livreur;
    }

    public Intersection getAdresse() {
        return adresse;
    }

    @Override
    public String toString() {
        return String.format("Livraison{numero=%d, fenetreDeLivraison=%s, "
                        + "livreur=%s, adresse=%s}",
                numero, fenetreDeLivraison, livreur, adresse);
    }
}
