package com.github.hexanome4114.pldagile.modele;

import java.util.List;

public final class Tournee {

    private Livreur livreur;

    /**
     * @livraisons liste des livraison à effectuer dans l'ordre du TSP
     */
    private List<Livraison> livraisons;
    /**
     * @itineraires liste des itinéraire à effectuer dans l'ordre des livraisons
     *              La première intersection du premier segment de chaque itinéraire
     *              correspond à une intersection de livraison. Et inversement pour la toute dernière.
     */
    private List<Itineraire> itineraires;

    public Tournee(Livreur livreur, List<Livraison> livraisons, List<Itineraire> itineraires) {
        this.livreur = livreur;
        this.livraisons = livraisons;
        this.itineraires = itineraires;
    }

    public Livreur getLivreur() {
        return livreur;
    }

    public List<Livraison> getLivraisons() {
        return livraisons;
    }

    public List<Itineraire> getItineraires() {
        return itineraires;
    }
}
