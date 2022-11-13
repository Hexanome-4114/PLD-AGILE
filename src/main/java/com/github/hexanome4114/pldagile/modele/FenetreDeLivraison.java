package com.github.hexanome4114.pldagile.modele;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FenetreDeLivraison that = (FenetreDeLivraison) o;
        return debut == that.debut && fin == that.fin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debut, fin);
    }
}
