package com.github.hexanome4114.pldagile.modele;

public final class FenetreDeLivraison {

    private final int debut;
    private final int fin;

    public FenetreDeLivraison(final int debut, final int fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public int getDebut() {
        return debut;
    }

    public int getFin() {
        return fin;
    }

    @Override
    public String toString() {
        return String.format("%dh-%dh", debut, fin);
    }
}
