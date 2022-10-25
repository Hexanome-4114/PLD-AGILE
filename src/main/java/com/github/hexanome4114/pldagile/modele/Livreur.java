package com.github.hexanome4114.pldagile.modele;

public class Livreur {
    private int numero;
    private  int vitesseMoyenne;

    public Livreur(int numero, int vitesseMoyenne) {
        this.numero = numero;
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public int getNumero() {
        return numero;
    }

    public int getVitesseMoyenne() {
        return vitesseMoyenne;
    }
}
