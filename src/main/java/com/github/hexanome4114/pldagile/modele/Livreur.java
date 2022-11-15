package com.github.hexanome4114.pldagile.modele;

public final class Livreur {

    private final int numero;
    private final int vitesseMoyenne;

    public Livreur(final int numero, final int vitesseMoyenne) {
        this.numero = numero;
        this.vitesseMoyenne = vitesseMoyenne;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Livreur livreur = (Livreur) o;

        return numero == livreur.numero;
    }

    @Override
    public int hashCode() {
        return numero;
    }

    public int getNumero() {
        return numero;
    }

    public int getVitesseMoyenne() {
        return vitesseMoyenne;
    }

    @Override
    public String toString() {
        return String.format("Livreur %d", numero);
    }
}
