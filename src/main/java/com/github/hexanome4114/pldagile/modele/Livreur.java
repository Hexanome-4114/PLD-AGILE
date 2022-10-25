package com.github.hexanome4114.pldagile.modele;

public final class Livreur {

    private final int numero;
    private final int vitesseMoyenne;

    public Livreur(final int numero, final int vitesseMoyenne) {
        this.numero = numero;
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public int getNumero() {
        return numero;
    }

    public int getVitesseMoyenne() {
        return vitesseMoyenne;
    }

    @Override
    public String toString() {
        return String.format(
                "Livreur{numero=%d, vitesseMoyenne=%d}", numero, vitesseMoyenne
        );
    }
}
