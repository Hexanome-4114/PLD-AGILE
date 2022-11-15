package com.github.hexanome4114.pldagile.modele;

import java.time.LocalTime;

public final class Livraison {

    private final int numero;
    private final FenetreDeLivraison fenetreDeLivraison;
    private final Livreur livreur;
    private final Intersection adresse;
    private LocalTime heurePassage;
    private boolean enRetard;

    public Livraison(final int numero,
                     final FenetreDeLivraison fenetreDeLivraison,
                     final Livreur livreur, final Intersection adresse) {
        this.numero = numero;
        this.fenetreDeLivraison = fenetreDeLivraison;
        this.livreur = livreur;
        this.adresse = adresse;
        this.enRetard = false;
    }

    public int getNumero() {
        return numero;
    }

    public LocalTime getHeurePassage() {
        return heurePassage;
    }

    public void setHeurePassage(final LocalTime heurePassage) {
        this.heurePassage = heurePassage;
    }

    public boolean isEnRetard() {
        return enRetard;
    }

    public void setEnRetard(final boolean enRetard) {
        this.enRetard = enRetard;
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
