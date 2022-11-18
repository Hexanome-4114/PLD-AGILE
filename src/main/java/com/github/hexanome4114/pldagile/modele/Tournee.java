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
     * Le livreur qui effectue les livraisons.
     */
    private final Livreur livreur;

    private final int tempsParLivraison;

    /**
     * La liste des livraison à effectuer, elles sont dans l'ordre du
     * TSP une fois la tournée calculée.
     */
    private List<Livraison> livraisons;

    /**
     * La liste des itinéraire à effectuer dans l'ordre des livraisons.
     * La première intersection du premier segment de chaque itinéraire
     * correspond à une intersection de livraison. Et inversement pour la
     * toute dernière.
     */
    private List<Itineraire> itineraires;

    private final Plan plan;

    private Intersection pointDepart;
    private boolean tourneeCalculee = false;


    public Tournee(final Livreur livreur, final List<Livraison> livraisons,
                   final Plan plan, final int tempsParLivraison,
                   final Intersection pointDepart) {
        this.livreur = livreur;
        this.livraisons = livraisons;
        this.plan = plan;
        this.tempsParLivraison = tempsParLivraison;
        this.pointDepart = pointDepart;
    }

    public Intersection getPointDepart() {
        return pointDepart;
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
     * @param fdlDepart fenetre de livraison de départ
     */
    public void calculerTournee(final FenetreDeLivraison fdlDepart) {

        // Dijkstra
        // Creation de chaque sommet et ajout dans le graphe
        Graphe graphe = TourneeHelper.creerGrapheDijkstra(this.plan);


        // Ajout d'une livraison factice au point de depart
        Livraison livraisonPointDepart = new Livraison(Integer.MAX_VALUE,
                fdlDepart, this.livreur, pointDepart);
        List<Livraison> livraisonsTemporaires = new ArrayList<>(
                this.livraisons);
        livraisonsTemporaires.add(0, livraisonPointDepart);

        // Création du graphe et calcul de tous les itinéraires nécessaires
        // pour l'affichage

        Pair<Graphe, Map<String, Itineraire>> resultCreationGrapheTSP =
                TourneeHelper.creerGrapheTSP(livraisonsTemporaires,
                        this.plan.getIntersections(), graphe);
        Graphe grapheTSP = resultCreationGrapheTSP.getKey();
        Map<String, Itineraire> itineraireMap = resultCreationGrapheTSP
                .getValue();

        int nbSommetsDansGrapheTSP = livraisonsTemporaires.size();

        GrapheTSP g = TourneeHelper.convertirGrapheVersGrapheComplet(grapheTSP);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        List<Livraison> livraisonsTSP = new ArrayList<>(this.livraisons.size());
        List<Itineraire> itinerairesFinaux = new ArrayList<>();

        // s'il y a une solution
        if (tsp.getSolutionCost() != Integer.MAX_VALUE) {
            // On garde uniquement les itinéraires que nous avons besoin et
            // on crée la tournée à renvoyer
            for (int i = 0; i < nbSommetsDansGrapheTSP - 1; ++i) {
                String idSommet = g.getMapIndexVersNomSommet()
                        .get(tsp.getSolution(i));
                String idSommet1 = g.getMapIndexVersNomSommet()
                        .get(tsp.getSolution(i + 1));
                Itineraire itineraire = itineraireMap
                        .get(idSommet + "_" + idSommet1);
                itinerairesFinaux.add(itineraire);

                // Ajoute la livraison dans l'ordre de passage
                for (Livraison livraison : this.livraisons) {
                    // on n'ajoute pas la livraison factice livraisonPointDepart
                    if (livraison.getAdresse().getId() == idSommet1) {
                        livraisonsTSP.add(livraison);
                        break;
                    }
                }
            }

            // Ajout du dernier itinéraire entre la dernière adresse de
            // livraison visité et la première (l'entrepôt)
            String idPremierSommet = g.getMapIndexVersNomSommet().get(0);
            String idDernierSommet = g.getMapIndexVersNomSommet().get(
                    tsp.getSolution(nbSommetsDansGrapheTSP - 1));
            itinerairesFinaux.add(itineraireMap.get(idDernierSommet + "_"
                    + idPremierSommet));

            this.livraisons = livraisonsTSP;
            this.itineraires = itinerairesFinaux;
            calculerHeuresPassagesLivraisons();

            this.tourneeCalculee = true;
        }
    }

    /**
     * Met à jour les heures de passage des livraisons de la tournée.
     */
    private void calculerHeuresPassagesLivraisons() {

        LocalTime heureCourante = LocalTime.of(8, 0);
        // Les livraisons et les itinéraires sont triés dans l'ordre de passage
        for (int i = 0; i < livraisons.size(); ++i) {
            int longueurItineraire = itineraires.get(i).getLongueur();
            int vitesse = this.livreur.getVitesseMoyenne();
            // Conversion km/h en cm/minute
            double vitesseConvertie = vitesse * (Math.pow(10, 5) / 60);
            int nbMinutesTrajet = (int) Math.ceil(
                    longueurItineraire / vitesseConvertie);
            heureCourante = heureCourante.plusMinutes(nbMinutesTrajet);

            Livraison livraisonCourante = this.livraisons.get(i);

            // Si la fenêtre de livraison de la livraison est plus tard, le
            // livreur attend jusqu'à l'heure de début de la fenêtre
            int debutFenetreLivraison = livraisonCourante
                    .getFenetreDeLivraison().getDebut();
            if (debutFenetreLivraison > heureCourante.getHour()) {
                heureCourante = LocalTime.of(debutFenetreLivraison, 0);
                livraisonCourante.setHeurePassage(heureCourante);
            } else {
                // Si la fenêtre de livraison est dépassée, on marque la
                // livraison comme non valide pour l'indiquer à l'utilisateur
                if (livraisonCourante.getFenetreDeLivraison().getFin()
                        <= heureCourante.getHour()) {
                    livraisonCourante.setEnRetard(true);
                }
                livraisonCourante.setHeurePassage(heureCourante);
            }

            this.livraisons.set(i, livraisonCourante);
            // On incrémente de 5min le nombre de minutes (temps prévu pour
            // la livraison)
            heureCourante = heureCourante.plusMinutes(this.tempsParLivraison);
        }
    }

    /**
     * Supprime la livraison de la tournée et calcule l'itinéraire
     * nécessaire pour afficher la tournée Recalcule l'heure de passage.
     * @param livraison livraison à supprimer de la tournée
     */
    public void supprimerLivraisonApresCalcul(final Livraison livraison) {
        if (!this.tourneeCalculee) {
            return;
        }
        // On cherche l'indice de la livraison à supprimer pour récupérer celle
        // d'avant et celle d'après
        int indiceLivraison = -2;
        for (int i = 0; i < this.livraisons.size(); ++i) {
            if (this.livraisons.get(i).getNumero() == livraison.getNumero()) {
                indiceLivraison = i;
            }
        }
        // On sépare le cas pour faire attention au cas où on part du
        // pointDepart ou si on y revient (premier et dernier trajet)
        Intersection intersectionAvant;
        Intersection intersectionApres;
        // Dans le cas où il ne reste qu'une seule livraison, on vide la
        // dernière restante et les 2 derniers itinéraires restants
        if (indiceLivraison == 0 && this.livraisons.size() == 1) {
            this.livraisons.remove(0);
            this.itineraires.clear();
        } else {
            intersectionAvant = getAdresseLivraisonAvant(indiceLivraison);
            intersectionApres = getAdresseIntersectionApres(indiceLivraison);

            // On récupère l'itinéraire entre intersectionAvant et
            // intersectionApres
            Map<Intersection, Itineraire> itinerairesMap = this.plan
                    .getItineraire(intersectionAvant);
            Itineraire itineraire = itinerairesMap.get(intersectionApres);

            // On supprime la livraison et les itinéraires puis on rajoute
            // la nouvelle
            this.livraisons.remove((indiceLivraison));
            this.itineraires.remove(indiceLivraison + 1);
            this.itineraires.remove(indiceLivraison);
            this.itineraires.add(indiceLivraison, itineraire);

            this.calculerHeuresPassagesLivraisons();
        }
    }

    /**
     * Ajoute une livraison au rang passé en paramètre dans la tournée.
     * Recalcule l'heure de passage.
     *
     * @param rang représente la x ième livraison que l'on souhaite effectuer.
     * @param livraisonAAjouter la livraison à ajouter.
     */
    public void ajouterLivraisonApresCalcul(
            final int rang,
            final Livraison livraisonAAjouter
    ) throws Exception {
        if (!this.tourneeCalculee) {
            return;
        }
        int indice = rang - 1;
        Intersection intersectionAAjouter = livraisonAAjouter.getAdresse();
        Intersection intersectionAvant = getAdresseLivraisonAvant(indice);
        Intersection intersectionApres;
        // Si on veut ajouter à la fin de la liste des livraison
        if (indice == this.livraisons.size()) {
            intersectionApres = this.getPointDepart();
        } else {
            intersectionApres = this.livraisons.get(indice).getAdresse();
        }
        // Calculer l'itinéraire entre intersectionAvant et intersectionAAjouter
        Map<Intersection, Itineraire> itinerairesMap
                = this.plan.getItineraire(intersectionAvant);
        Itineraire itineraireAvantAAjouter
                = itinerairesMap.get(intersectionAAjouter);
        // Si il n'y a pas d'itinéraire entre les 2, throw une exception.
        if (itineraireAvantAAjouter.getIntersections().isEmpty()) {
            throw new Exception(
                    "Il manque un itinéraire entre l'adresse précédente "
                            + "et l'adresse de livraison."
            );
        }

        // Calculer l'itinéraire entre intersectionAAjouter et intersectionApres
        itinerairesMap = this.plan.getItineraire(intersectionAAjouter);
        Itineraire itineraireAAjouterApres
                = itinerairesMap.get(intersectionApres);
        // Si il n'y a pas d'itinéraire entre les 2, throw une exception.
        if (itineraireAAjouterApres.getIntersections().isEmpty()) {
            throw new Exception(
                    "Il manque un itinéraire entre l'adresse de livraison "
                            + "et l'adresse."
            );
        }

        // Ajouter la livraison dans la liste des livraisons
        this.livraisons.add(indice, livraisonAAjouter);

        // Remplacer l'itineraire entre intersectionAvant et intersectionApres
        // par l'itineraire entre intersectionAvant et intersectionAAjouter
        this.itineraires.set(indice, itineraireAvantAAjouter);

        // Ajouter l'itinéraire entre intersectionAAjouter et intersectionApres
        this.itineraires.add(indice + 1, itineraireAAjouterApres);

        calculerHeuresPassagesLivraisons();
    }

    /**
     * Retourne l'adresse de livraison précédente.
     * Cette fonction requiert que la tournée ait été calculée.
     * @param indice indice de la livraison dans la liste des livraisons
     * @return <ul>
     *     <li>
     *         null si (
     *         !this.tourneeCalculee
     *         || indice < 0
     *         || indice >= this.livraisons.size()
     *         )
     *     </li>
     *     <li>
     *         L'adresse de livraison précédent la livraison présente
     *         à l'indice i
     *     </li>
     *     <li>
     *         Le point de départ de la tournée si indice==0
     *     </li></>
     * </ul>
     */
    private Intersection getAdresseLivraisonAvant(final int indice) {
        if (
                !this.tourneeCalculee
                || indice < 0
                || indice >= this.livraisons.size()
        ) {
            return null;
        }
        Intersection result;
        if (indice == 0) {
            result = this.pointDepart;
        } else {
            result = this.livraisons.get(indice - 1).getAdresse();
        }
        return result;
    }

    /**
     * Retourne l'adresse de livraison subséquente.
     *
     * Cette fonction requiert que la tournée ai été calculée.
     * @param indice indice de la livraison dans la liste des livraisons
     * @return <ul>
     *     <li>
     *         null si (
     *         !this.tourneeCalculee
     *         || indice < 0
     *         || indice >= this.livraisons.size())
     *     </li>
     *     <li>
     *         L'adresse de livraison subséquente de la livraison présente
     *         à l'indice i
     *     </li>
     *     <li>
     *         Le point de départ de la tournée
     *         si indice==this.livraisons.size()-1
     *     </li></>
     * </ul>
     */
    private Intersection getAdresseIntersectionApres(final int indice) {
        Intersection result;
        if (
                !this.tourneeCalculee
                || indice < 0
                || indice >= this.livraisons.size()
        ) {
            result = null;
        }
        if (indice == this.livraisons.size() - 1) {
            result = this.pointDepart;
        } else {
            result = this.livraisons.get(indice + 1).getAdresse();
        }
        return result;
    }
}
