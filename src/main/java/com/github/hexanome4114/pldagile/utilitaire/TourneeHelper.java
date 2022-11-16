package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.algorithme.tsp.GrapheTSP;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Tournee;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Itineraire;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import javafx.util.Pair;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class TourneeHelper {

    private TourneeHelper() { }

    public static Graphe creerGrapheDijkstra(final Tournee tournee) {
        Graphe graphe = new Graphe();
        for (String intersectionId : tournee.getPlan().getIntersections().keySet()) {
            Sommet sommet = new Sommet(intersectionId);
            graphe.ajouterSommet(sommet);
        }

        // ajout des sommets adjacents et de la distance
        for (Intersection intersection : tournee.getPlan().getIntersections()
                .values()) {
            for (Map.Entry<Intersection, Pair<Integer, String>> set
                    : intersection.getIntersections().entrySet()) {
                Sommet sommetOrigine = graphe.getSommets()
                        .get(intersection.getId());
                Sommet sommetDestination = graphe.getSommets()
                        .get(set.getKey().getId());
                sommetOrigine.addDestination(sommetDestination,
                        set.getValue().getKey());
            }
        }
        return graphe;
    }

    /**
     * Crée le graphe à donner en paramètre au TSP.
     *      * Calcule aussi les itinéraires précis entre chaque point de livraison
     *      * de la tournée.
     * @param livraisons
     * @param intersections
     * @param grapheDijkstra
     * @return La map d'itinéraire est une map dont la clé est de la forme intersection1Id_intersection2Id et la valeur
     * est l'itinéraire pour aller de l'intersection1 à l'intersection2
     */
    public static Pair<Graphe, Map<String, Itineraire>> creerGrapheTSP(final List<Livraison> livraisons,
                                                                       final HashMap<String, Intersection> intersections,
                                                                       Graphe grapheDijkstra) {
        // Création du graphe utilisé pour appeler le TSP
        Graphe grapheTSP = new Graphe();
        for (Livraison livraison : livraisons) {
            grapheTSP.ajouterSommet(new Sommet(livraison.getAdresse().getId()));
        }

        // Numérotation des FenetreDeLivraison
        Map<Integer, FenetreDeLivraison> entierVersFenetreDeLivraison = new HashMap<>();
        Map<FenetreDeLivraison, Integer> fenetreDeLivraisonVersEntier = new HashMap<>();
        int compteurFdL = 0;
        for (Livraison livraison : livraisons) { // On assigne un numéro à chaque fenêtre de livraison
            if (fenetreDeLivraisonVersEntier.get(livraison.getFenetreDeLivraison()) == null) {
                compteurFdL += 1;
                entierVersFenetreDeLivraison.put(compteurFdL, livraison.getFenetreDeLivraison());
                fenetreDeLivraisonVersEntier.put(livraison.getFenetreDeLivraison(), compteurFdL);
            }
        }

        // itineraireMap stocke l'itinéraire entre intersection1 vers intersection2, pour chaque point de livraison
        // key est de la sorte "intersection1.id_intersection2.id"
        Map<String, Itineraire> itineraireMap = new HashMap<>();

        for (Livraison livraisonCourante : livraisons) {
            int numFdLCourante = fenetreDeLivraisonVersEntier.get(livraisonCourante.getFenetreDeLivraison());

            // On calcule la distance entre l'adresse de livraison courante (sommetSource) et les autres adresse de livraisons
            Sommet sommetSource = grapheDijkstra.getSommets().get(livraisonCourante.getAdresse().getId());

            // Calcul des plus courts chemins entre le sommet source et les autres sommets
            grapheDijkstra = Graphe.calculerCheminplusCourtDepuisSource(grapheDijkstra, sommetSource);

            // Récupération des pcc pour construire le graphe TSP et les itinéraires précis correspondants.
            for (Livraison autreLivraison : livraisons) {
                if (autreLivraison.getNumero() != livraisonCourante.getNumero()) {

                    int numAutreFdL = fenetreDeLivraisonVersEntier.get(autreLivraison.getFenetreDeLivraison());
                    // On garde les arcs uniquement si les livraisons sont dans la même FdL
                    // ou si autreLivraison est dans la FdL directement subséquente.
                    // ou si c'est la dernière FdL et que c'est un chemin vers le point d'arrivée (aka l'entrepot)
                    if (numFdLCourante == numAutreFdL || numAutreFdL == (numFdLCourante + 1)
                            || (numFdLCourante == compteurFdL && autreLivraison.getNumero() == livraisons.get(0).getNumero())) {
                        // Sommet correspondant au point de passage dans le graphe dijktra
                        Sommet sommetLivraison = grapheDijkstra.getSommets().get(autreLivraison.getAdresse().getId());

                        // Création de l'itinéraire
                        List<Intersection> intersectionsItineraire = new ArrayList<>(sommetLivraison.getCheminPlusCourt().size());
                        List<Sommet> plusCourtCheminCourant = sommetLivraison.getCheminPlusCourt();

                        // S'il n'y a pas de chemin plus court entre les 2, on n'ajoute pas
                        if (!plusCourtCheminCourant.isEmpty()) {
                            for (Sommet sommet : plusCourtCheminCourant) {
                                Intersection intersectionDuPcc = intersections.get(sommet.getNom());
                                intersectionsItineraire.add(intersectionDuPcc);
                            }
                            intersectionsItineraire.add(autreLivraison.getAdresse()); // On doit ajouter l'intersection destination
                            itineraireMap.put(livraisonCourante.getAdresse().getId() + "_" + autreLivraison.getAdresse().getId(),
                                    new Itineraire(intersectionsItineraire));

                            // Sommets dans le graph complet
                            Sommet sommetSourceDansGrapheComplet = grapheTSP.getSommets().get(sommetSource.getNom());
                            Sommet sommetDestDansGrapheComplet = grapheTSP.getSommets().get(sommetLivraison.getNom());
                            sommetSourceDansGrapheComplet.addDestination(sommetDestDansGrapheComplet,
                                    sommetLivraison.getDistance());
                        }
                    }
                }
            }
            // réinitialisation des sommets du graphe pour le nouvel appel
            grapheDijkstra.reinitialiserSommetsGraphe();
        }
        return new Pair<>(grapheTSP, itineraireMap);
    }

    public static GrapheTSP convertirGrapheVersGrapheComplet(final Graphe grapheEntrant) {

        // Création des champs de la classe GrapheTSP
        int nbVertices = grapheEntrant.getSommets().size();
        int[][] cost = new int[nbVertices][nbVertices];
        Map<String, Integer> mapNomSommetVersIndex = new LinkedHashMap<>();
        Map<Integer, String> mapIndexVersNomSommet = new LinkedHashMap<>();

        // Conversion entre les deux types de graphes
        for (int i = 0; i < nbVertices; i++) {
            for (int j = 0; j < nbVertices; j++) {
                cost[i][j] = -1;
            }
        }
        Sommet unSommet;
        int sommetOrigine;
        int sommetDest;
        int iter = 0;

        for (String nom : grapheEntrant.getSommets().keySet()) {
            mapNomSommetVersIndex.put(nom, iter);
            mapIndexVersNomSommet.put(iter, nom);
            iter++;
        }
        //on itère sur les sommets du graphe
        for (Map.Entry entry : grapheEntrant.getSommets().entrySet()) {
            unSommet = (Sommet) entry.getValue();
            // Pour chaque sommet, on récupére les arc associés et les ajoute
            // dans la matrice des arcs
            for (Map.Entry<Sommet, Integer> arc : unSommet
                    .getSommetsAdjacents().entrySet()) {
                sommetOrigine = mapNomSommetVersIndex.get(unSommet.getNom());
                sommetDest = mapNomSommetVersIndex.get(arc.getKey().getNom());
                cost[sommetOrigine][sommetDest] = arc.getValue();
            }
        }
        return new GrapheTSP(nbVertices, cost, mapNomSommetVersIndex, mapIndexVersNomSommet);
    }
}
