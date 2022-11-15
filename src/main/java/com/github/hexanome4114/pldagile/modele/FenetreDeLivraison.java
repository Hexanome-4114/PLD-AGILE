package com.github.hexanome4114.pldagile.modele;

public enum FenetreDeLivraison {

    H8_H9(8, 9),
    H9_H10(9, 10),
    H10_H11(10, 11),
    H11_H12(11, 12);

    private final int debut;
    private final int fin;

    FenetreDeLivraison(final int debut, final int fin) {
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
