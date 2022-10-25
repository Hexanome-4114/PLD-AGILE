package com.github.hexanome4114.pldagile.modele;

public class FenetreDeLivraison {
    private int debut;
    private int fin;

    public FenetreDeLivraison(int debut, int fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public int getDebut() {
        return debut;
    }

    public int getFin() {
        return fin;
    }
}
