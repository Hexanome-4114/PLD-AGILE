package com.github.hexanome4114.pldagile.modele;

public enum Livreur {

    LIVREUR_1(1),
    LIVREUR_2(2),
    LIVREUR_3(3),
    LIVREUR_4(4);

    private final int numero;

    private static final int VITESSE_MOYENNE = 15;

    Livreur(final int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public int getVitesseMoyenne() {
        return VITESSE_MOYENNE;
    }

    @Override
    public String toString() {
        return String.format("Livreur %d", numero);
    }
}
