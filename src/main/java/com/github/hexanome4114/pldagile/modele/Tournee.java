package com.github.hexanome4114.pldagile.modele;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.tsp.GrapheTSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP1;
import com.github.hexanome4114.pldagile.utilitaire.TourneeHelper;
import javafx.util.Pair;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Tournee {

    /**
     * @livreur livreur qui effectue les livraisons
     */
    private Livreur livreur;

    private int tempsParLivraison;

    /**
     * @livraisons liste des livraison à effectuer, elles sont dans l'ordre du TSP une fois la tournée calculée
     */
    private List<Livraison> livraisons;
    /**
     * @itineraires liste des itinéraire à effectuer dans l'ordre des livraisons
     * La première intersection du premier segment de chaque itinéraire
     * correspond à une intersection de livraison. Et inversement pour la toute dernière.
     */
    private List<Itineraire> itineraires;
    private Plan plan;


    public Tournee(final Livreur livreur, final List<Livraison> livraisons, final Plan plan, final int tempsParLivraison) {
        this.livreur = livreur;
        this.livraisons = livraisons;
        this.plan = plan;
        this.tempsParLivraison = tempsParLivraison;
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

    public Plan getPlan() {
        return plan;
    }

    /**
     * Calcule une Tournée qui part et revient sur la première livraison,
     * le livreur partira à l'heure de la première livraison.
     *
     * @param pointDepart intersection de départ et d'arrivée de la tournée (doit être différents de la premiere livraison)
     * @param fdlDepart fenetre de livraison de départ
     */
    public void calculerTournee(final Intersection pointDepart, final FenetreDeLivraison fdlDepart) {

        // Dijkstra
        // Creation de chaque sommet et ajout dans le graphe
        Graphe graphe = TourneeHelper.creerGrapheDijkstra(this);


        // Ajout d'une livraison factice au point de depart
        Livraison livraisonPointDepart = new Livraison(Integer.MAX_VALUE, fdlDepart, this.livreur, pointDepart);
        List<Livraison> livraisonsTemporaires = new ArrayList<>(this.livraisons);
        livraisonsTemporaires.add(0, livraisonPointDepart);

        // Création du graphe et calcul de tous les itinéraires nécessaires pour l'affichage

        Pair<Graphe, Map<String, Itineraire>> resultCreationGrapheTSP = TourneeHelper.creerGrapheTSP(livraisonsTemporaires,
                this.plan.getIntersections(), graphe);
        Graphe grapheTSP = resultCreationGrapheTSP.getKey();
        Map<String, Itineraire> itineraireMap = resultCreationGrapheTSP.getValue();

        int nbSommetsDansGrapheTSP = livraisonsTemporaires.size();

        GrapheTSP g = TourneeHelper.convertirGrapheVersGrapheComplet(grapheTSP);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        List<Livraison> livraisonsTSP = new ArrayList<>(this.livraisons.size());
        List<Itineraire> itinerairesFinaux = new ArrayList<>();
        if (tsp.getSolutionCost() != Integer.MAX_VALUE) { // s'il y a une solution
            // On garde uniquement les itinéraires que nous avons besoin et on crée la tournée à renvoyer
            for (int i = 0; i < nbSommetsDansGrapheTSP - 1; ++i) {
                String idSommet = g.getMapIndexVersNomSommet().get(tsp.getSolution(i));
                String idSommet1 = g.getMapIndexVersNomSommet().get(tsp.getSolution(i + 1));
                Itineraire itineraire = itineraireMap.get(idSommet + "_" + idSommet1);
                itinerairesFinaux.add(itineraire);
                // Ajoute la livraison dans l'ordre de passage
                for (Livraison livraison : this.livraisons) {
                    if (livraison.getAdresse().getId() == idSommet1) { // on n'ajoute pas la livraison factice 'livraisonPointDepart'
                        livraisonsTSP.add(livraison);
                        break;
                    }
                }
            }
            // Ajout du dernier itinéraire entre la dernière adresse de livraison visité et la première (l'entrepôt)
            String idPremierSommet = g.getMapIndexVersNomSommet().get(0);
            String idDernierSommet = g.getMapIndexVersNomSommet().get(tsp.getSolution(nbSommetsDansGrapheTSP - 1));
            itinerairesFinaux.add(itineraireMap.get(idDernierSommet + "_" + idPremierSommet));

            this.livraisons = livraisonsTSP;
            this.itineraires = itinerairesFinaux;
            calculerHeuresPassagesLivraisons();
        }
    }

    /**
     * Met à jour les heures de passage des livraisons de la tournée.
     */
    public void calculerHeuresPassagesLivraisons() {

        LocalTime heureCourante  = LocalTime.of(8, 0);
        // Les livraisons et les itinéraires sont triés dans l'ordre de passage
        for (int i = 0; i < livraisons.size(); ++i) {
            int longueurItineraire = itineraires.get(i).getLongueur();
            int vitesse = this.livreur.getVitesseMoyenne();
            // Conversion km/h en cm/minute
            double vitesseConvertie = vitesse * (Math.pow(10, 5) / 60);
            int nbMinutesTrajet = (int) Math.ceil(longueurItineraire / vitesseConvertie);
            heureCourante = heureCourante.plusMinutes(nbMinutesTrajet);

            Livraison livraisonCourante = this.livraisons.get(i);

            // Si la fenêtre de livraison de la livraison est plus tard, le livreur attend jusqu'à
            // l'heure de début de la fenêtre
            int debutFenetreLivraison = livraisonCourante.getFenetreDeLivraison().getDebut();
            if (debutFenetreLivraison > heureCourante.getHour()) {
                heureCourante = LocalTime.of(debutFenetreLivraison, 0);
                livraisonCourante.setHeurePassage(heureCourante);
            } else {
                // Si la fenêtre de livraison est dépassée, on marque la livraison comme non valide pour
                // l'indiquer à l'utilisateur
                if (livraisonCourante.getFenetreDeLivraison().getFin() <= heureCourante.getHour()) {
                    livraisonCourante.setEnRetard(true);
                }
                livraisonCourante.setHeurePassage(heureCourante);
            }

            this.livraisons.set(i, livraisonCourante);
            // On incrémente de 5min le nombre de minutes (temps prévu pour la livraison)
            heureCourante = heureCourante.plusMinutes(this.tempsParLivraison);
        }

        // Gestion à part du dernier itinéraire de retour vers l'entrepôt
    }
}
