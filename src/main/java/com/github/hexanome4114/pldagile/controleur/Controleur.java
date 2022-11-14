package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.algorithme.tsp.CompleteGraph;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP1;
import com.github.hexanome4114.pldagile.modele.*;
import com.github.hexanome4114.pldagile.utilitaire.CalquePlan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import com.gluonhq.maps.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {

    /**
     * Vitesse de déplacement d'un livreur.
     */
    public static final int VITESSE_MOYENNE = 15;

    /**
     * Nombre de livreurs disponibles par défaut.
     */
    private static final int NOMBRE_LIVREURS = 1;

    private List<Livreur> livreurs;

    private List<FenetreDeLivraison> fenetresDeLivraison;

    private Circle pointClique;
    private Plan plan;

    /**
     * Vue de l'application.
     */
    private Stage stage;

    @FXML
    private Pane carte;

    @FXML
    private ComboBox<Livreur> comboBoxLivreur;

    @FXML
    private ComboBox<FenetreDeLivraison> comboBoxFenetreDeLivraison;

    @FXML
    private ComboBox<Intersection> comboBoxAdresse;

    @FXML
    private TableView<Livraison> tableauLivraison;

    @FXML
    private TableColumn<Livraison, Integer> numeroLivraisonColonne;

    @FXML
    private TableColumn<Livraison, Livreur> livreurColonne;

    @FXML
    private TableColumn<Livraison, FenetreDeLivraison> fenetreDeLivraisonColonne;

    @FXML
    private Label messageErreur;

    @FXML
    private Button supprimerLivraisonBouton;

    @FXML
    private Button sauvegarderLivraisonsBouton;

    @FXML
    public void initialize() {
        System.setProperty("javafx.platform", "desktop");

        this.genererLivreurs(NOMBRE_LIVREURS);
        this.fenetresDeLivraison = new ArrayList<>(Arrays.asList(
                new FenetreDeLivraison(8, 9),
                new FenetreDeLivraison(9, 10),
                new FenetreDeLivraison(10, 11),
                new FenetreDeLivraison(11, 12)
        ));

        ObservableList<FenetreDeLivraison> oListFenetreDeLivraison =
                FXCollections.observableArrayList(this.fenetresDeLivraison);
        ObservableList<Livreur> oListLivreurs =
                FXCollections.observableArrayList(this.livreurs);

        this.comboBoxLivreur.setPromptText("Livreur");
        this.comboBoxLivreur.setItems(oListLivreurs);
        this.comboBoxFenetreDeLivraison.setPromptText("Fenêtre de livraison");
        this.comboBoxFenetreDeLivraison.setItems(oListFenetreDeLivraison);

        this.numeroLivraisonColonne.setCellValueFactory(
                new PropertyValueFactory<>("numero"));
        this.livreurColonne.setCellValueFactory(
                new PropertyValueFactory<>("livreur"));
        this.fenetreDeLivraisonColonne.setCellValueFactory(
                new PropertyValueFactory<>("fenetreDeLivraison"));

        tableauLivraison.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancienneSelection, nouvelleSelection) -> {
                    // bouton cliquable que lorsqu'une livraison est
                    // sélectionnée
                    this.supprimerLivraisonBouton.setDisable(
                            nouvelleSelection == null);
                }
        );

        tableauLivraison.getItems().addListener(
                (ListChangeListener<Livraison>) (obs) -> {
                    // bouton cliquable que lorsqu'il y a des livraisons
                    this.sauvegarderLivraisonsBouton.setDisable(
                            obs.getList().isEmpty());
                }
        );
    }

    /**
     * Génère un ensemble de livreurs en fonction du nombre
     * souhaité.
     *
     * @param nbLivreurs le nombre de livreurs souhaité
     */
    private void genererLivreurs(final int nbLivreurs) {
        if (this.livreurs != null && nbLivreurs == this.livreurs.size()) {
            return; // déjà le bon nombre de livreurs
        }

        List<Livreur> livreurs = new ArrayList<>();

        for (int i = 1; i <= nbLivreurs; i++) {
            livreurs.add(new Livreur(i, VITESSE_MOYENNE));
        }

        this.livreurs = livreurs;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    public void ajouterLivraison() {
        if (this.comboBoxLivreur.getValue() != null
                && this.comboBoxFenetreDeLivraison.getValue() != null
                && this.comboBoxAdresse.getValue() != null) {

            List<Livraison> livraisons = this.tableauLivraison.getItems();

            int numero;

            if (livraisons.isEmpty()) {
                numero = 1;
            } else { // le numéro de la dernière livraison + 1
                numero = livraisons.get(livraisons.size() - 1)
                        .getNumero() + 1;
            }

            Livraison livraison = new Livraison(
                    numero,
                    this.comboBoxFenetreDeLivraison.getValue(),
                    this.comboBoxLivreur.getValue(),
                    this.comboBoxAdresse.getValue());

            livraisons.add(livraison);
            this.messageErreur.setText("");
        } else {
            this.messageErreur.setText("Veuillez renseigner tous les champs !");
        }
    }

    public void supprimerLivraison() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();
        this.tableauLivraison.getItems().remove(livraison);
    }

    public void sauvegarderLivraisons() {
        FileChooser selecteurFichier = new FileChooser();
        selecteurFichier.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
        );

        File fichier = selecteurFichier.showSaveDialog(this.stage);

        if (fichier == null) { // aucun fichier sélectionné
            return;
        }

        try {
            Serialiseur.sauvegarderLivraisons(
                    fichier, this.tableauLivraison.getItems());
        } catch (Exception e) {
            this.messageErreur.setText(
                    "Problème lors de la sauvegarde des livraisons.");
        }
    }

    public void chargerLivraisons() {
        if (!this.tableauLivraison.getItems().isEmpty()) {
            // des livraisons sont déjà présentes, on avertit l'utilisateur
            Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
            alerte.setHeaderText("Les livraisons existantes seront écrasées,"
                    + " êtes-vous sûr de vouloir continuer ?");

            Optional<ButtonType> option = alerte.showAndWait();

            if (option.get() != ButtonType.OK) {
                return; // annulation du chargement
            }
        }

        FileChooser selecteurFichier = new FileChooser();
        selecteurFichier.setTitle("Sélectionnez un fichier XML");
        selecteurFichier.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
        );

        File fichier = selecteurFichier.showOpenDialog(this.stage);

        if (fichier == null) { // aucun fichier sélectionné
            return;
        }

        try {
            List<Livraison> livraisons = Serialiseur.chargerLivraisons(fichier);
            // TODO valider les livraisons chargées
            this.tableauLivraison.setItems(
                    FXCollections.observableArrayList(livraisons));
        } catch (Exception e) {
            this.messageErreur.setText(
                    "Problème lors du chargement des livraisons.");
        }
    }

    public List<Tournee> calculerLesTournees() {
        List<Tournee> tournees = new ArrayList<>();
        // Pour chaque livreur, on appelle "calculerTournee"
        for(Livreur livreur : this.livreurs) {
            // on récupère les livraisons du livreur
            List<Livraison> livraisons = this.tableauLivraison.getItems().stream().filter(
                    livraison -> livraison.getLivreur().equals(livreur))
                    .collect(Collectors.toList());

            // Ajout de l'entrepôt comme départ(et arrivée) de la tournée.
            // TODO Est ce ok de créer une "fausse" livraison pour l'entrepôt ?
            Livraison livraisonEntrepot = new Livraison(Integer.MAX_VALUE, new FenetreDeLivraison(8, 9),
                    livreur, this.plan.getEntrepot());
            livraisons.add(0, livraisonEntrepot);
            tournees.add(calculerTournee(livreur, livraisons));
        }

        return tournees;
    }

    /**
     * Calcule une Tournée qui part et revient sur la première livraison
     *
     * @param livreur livreur qui effectue les livraisons
     * @param livraisons liste des livraisons à effectuer, la première livraison est le point de départ et d'arrivée.
     * @return Tournée
     */
    public Tournee calculerTournee(Livreur livreur, List<Livraison> livraisons) {
        // TODO Il faudrait un helper pour :
        //  * la traduction des Intersection vers un graphe Dijkstra
        //  * la traduction Graphe dijkstra vers CompleteGraph (qui remplacerait le constructeur dans CompleteGraph de tsp)
        // Dijkstra
        // Creation de chaque noeud et ajout dans le graphe
        Graphe graphe = new Graphe();
        for (String intersectionId : this.plan.getIntersections().keySet()) {
            Sommet sommet = new Sommet(intersectionId);
            graphe.ajouterSommet(sommet);
        }

        // ajout des sommets adjacents et de la distance
        for (Intersection intersection : this.plan.getIntersections()
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

        // Graphe complet utilisé pour le TSP
        int nbSommetsDansGrapheComplet = livraisons.size();
        Graphe grapheComplet = new Graphe();
        for (Livraison livraison : livraisons) {
            grapheComplet.ajouterSommet(new Sommet(livraison.getAdresse().getId()));
        }
        // Numérotation des FenetreDeLivraison
        Map<Integer, FenetreDeLivraison> entierVersFenetreDeLivraison = new HashMap<>();
        Map<FenetreDeLivraison, Integer> fenetreDeLivraisonVersEntier = new HashMap<>();
        int compteurFdL = 0;
        for(Livraison livraison : livraisons){ // On assigne un numéro à chaque fenêtre de livraison
            if(fenetreDeLivraisonVersEntier.get(livraison.getFenetreDeLivraison()) == null){
                compteurFdL += 1;
                entierVersFenetreDeLivraison.put(compteurFdL, livraison.getFenetreDeLivraison());
                fenetreDeLivraisonVersEntier.put(livraison.getFenetreDeLivraison(), compteurFdL);
            }
        }

        // Calcul de distance entre chaque adresse de livraison pour la
        // création d'un graphe complet
        // itineraireMap stocke l'itinéraire entre intersection1
        // vers intersection2
        // key est de la sorte "intersection1.id_intersection2.id"

        Map<String, Itineraire> itineraireMap = new HashMap<>();
        for (Livraison livraisonCourante : livraisons) {
            int numFdLCourante = fenetreDeLivraisonVersEntier.get(livraisonCourante.getFenetreDeLivraison());
            // On calcul la distance entre l'adresse de livraison courante et
            // les autres adresse de livraisons
            Sommet sommetSource = graphe.getSommets().get(livraisonCourante.getAdresse().getId()); // adresse de livraison courante
            // Calcul des plus courts chemins (pcc)
            graphe = Graphe.calculerCheminplusCourtDepuisSource(graphe, sommetSource); // contient les pcc entre le sommet source et les autres sommets
            // Récupération des pcc pour créer le graphe complet et construire les intinéraires correspondants.
            for (Livraison autreLivraison : livraisons) {
                if (autreLivraison.getNumero() != livraisonCourante.getNumero()) {
                    int numAutreFdL = fenetreDeLivraisonVersEntier.get(autreLivraison.getFenetreDeLivraison());
                    // On garde les arcs uniquement si les livraison sont dans la même FdL
                    // ou si autreLivraison est dans la FdL directement subséquente.
                    // ou si c'est la dernière FdL et que c'est un chemin vers le point de départ (aka l'entrepot)
                    if(numFdLCourante == numAutreFdL || numAutreFdL == (numFdLCourante +1)
                            || (numFdLCourante == compteurFdL && autreLivraison.getNumero() == livraisons.get(0).getNumero())){
                        // Sommet correspondant au point de passage dans le graphe dijktra
                        Sommet sommetLivraison = graphe.getSommets().get(autreLivraison.getAdresse().getId());
                        // Création de l'itinéraire
                        List<Intersection> intersectionsItineraire = new ArrayList<>(sommetLivraison.getCheminPlusCourt().size());
                        List<Sommet> cheminAdresseLivraisonCouranteVersAdresseLivraison = sommetLivraison.getCheminPlusCourt();

                        // S'il n'y a pas de chemin plus court entre les 2, on n'ajotue pas
                        if (!cheminAdresseLivraisonCouranteVersAdresseLivraison.isEmpty()) {
                            for (Sommet sommet : cheminAdresseLivraisonCouranteVersAdresseLivraison) {
                                Intersection intersectionDuPcc = this.plan.getIntersections().get(sommet.getNom());
                                intersectionsItineraire.add(intersectionDuPcc);
                            }
                            intersectionsItineraire.add(autreLivraison.getAdresse()); // On doit ajouter l'intersection destination
                            itineraireMap.put(livraisonCourante.getAdresse().getId() + "_" + autreLivraison.getAdresse().getId(),
                                    new Itineraire(intersectionsItineraire));

                            // Sommets dans le graph complet
                            // TODO Il y a un bug quand il n'y a pas d'itinéraire entre 2 sommets

                            Sommet sommetSourceDansGrapheComplet = grapheComplet.getSommets().get(sommetSource.getNom());
                            Sommet sommetDestDansGrapheComplet = grapheComplet.getSommets().get(sommetLivraison.getNom());
                            sommetSourceDansGrapheComplet.addDestination(sommetDestDansGrapheComplet,
                                    sommetLivraison.getDistance());
                        }
                    }
                }
            }
            // réinitialisation des sommets du graphe pour le nouvel appel
            graphe.reinitialiserSommetsGraphe();
        }

        CompleteGraph g = new CompleteGraph(grapheComplet);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        List<Itineraire> itinerairesFinaux = new ArrayList<>();
        // TODO réordonner les livraisons dans l'ordre des livraisons pour le TSP
        List<Livraison> livraisonsTSP = new ArrayList<>(livraisons.size());
        if(tsp.getSolutionCost() != Integer.MAX_VALUE){ // s'il y a une solution
            // On garde uniquement les itinéraires que nous avons besoin et on crée la tournée à renvoyer
            for(int i=0; i<nbSommetsDansGrapheComplet-1;++i){
                String idSommet = g.getMapIndexVersNomSommet().get(tsp.getSolution(i));
                String idSommet1 = g.getMapIndexVersNomSommet().get(tsp.getSolution(i+1));
                Itineraire itineraire = itineraireMap.get(idSommet+"_"+idSommet1);
                itinerairesFinaux.add(itineraire);
               //System.out.println("tsp.getSolution(i) = " + tsp.getSolution(i));
            }
            // Ajout du dernier itinéraire entre la dernière adresse de livraison visité et la première (l'entrepôt)
            String idPremierSommet = g.getMapIndexVersNomSommet().get(0);
            String idDernierSommet = g.getMapIndexVersNomSommet().get(tsp.getSolution(nbSommetsDansGrapheComplet-1));
            itinerairesFinaux.add(itineraireMap.get(idDernierSommet+"_"+idPremierSommet));
        }
        // TODO tournee = new Tournee(livreur, livraisonsTSP, itinerairesFinaux)
        Tournee tournee = new Tournee(livreur, livraisons, itinerairesFinaux);

        return tournee;
    }

    @FXML
    private void chargerPlan() {
        FileChooser selecteurFichier = new FileChooser();
        selecteurFichier.setTitle("Sélectionnez un fichier XML");
        selecteurFichier.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
        );

        File fichier = selecteurFichier.showOpenDialog(this.stage);

        if (fichier == null) { // aucun fichier sélectionné
            return;
        }

        try {
            Plan plan = Serialiseur.chargerPlan(fichier);
            this.plan = plan;
            this.afficherPlan(plan);
        } catch (DocumentException e) {
            this.messageErreur.setText("Problème lors du chargement du plan.");
        }
    }

    private void afficherPlan(final Plan plan) {
        CalquePlan calque = new CalquePlan();

        // intersections
        for (Intersection intersection : plan.getIntersections().values()) {
            calque.ajouterPoint(intersection, new Circle(3, Color.GREY));
        }

        calque.getPoints().forEach(point -> {
            // ajout d'un listener sur chaque point du calque
            point.getValue().setOnMouseClicked(e -> {
                e.consume();
                if (this.pointClique != null) {
                    this.pointClique.setFill(Color.GREY);
                }
                this.pointClique = (Circle) e.getTarget();
                ((Circle) e.getTarget()).setFill(Color.BLUE);
                this.comboBoxAdresse.setValue(point.getKey());
            });
        });

        // entrepot
        Intersection entrepot = plan.getEntrepot();
        calque.ajouterPoint(entrepot, new Circle(5, Color.RED));

        // config carte
        MapView carteVue = new MapView();

        carteVue.setZoom(14.5);
        carteVue.flyTo(0, entrepot, 0.1); // centre la carte sur l'entrepot
        carteVue.addLayer(calque); // ajout du calque contenant les points

        StackPane sp = new StackPane();
        sp.setPrefSize(carte.getPrefWidth(), carte.getPrefHeight());
        sp.getChildren().add(carteVue);

        this.carte.getChildren().add(sp);
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }
}
