package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.algorithme.tsp.CompleteGraph;
import com.github.hexanome4114.pldagile.algorithme.tsp.Graph;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP1;
import com.github.hexanome4114.pldagile.modele.*;
import com.github.hexanome4114.pldagile.utilitaire.CalquePlan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import com.gluonhq.maps.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

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

    private CalquePlan calquePlan;

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
    private TableColumn<Livraison, Integer> numeroLivraison;

    @FXML
    private TableColumn<Livraison, Livreur> livreur;

    @FXML
    private TableColumn<Livraison, FenetreDeLivraison> fenetreDeLivraison;

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

        this.numeroLivraison.setCellValueFactory(
                new PropertyValueFactory<>("numero"));
        this.livreur.setCellValueFactory(
                new PropertyValueFactory<>("livreur"));
        this.fenetreDeLivraison.setCellValueFactory(
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

    public void calculerTournee() {

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

        // Utilisation d'une liste itermédiaire pour prendre en compte
        // l'entrepôt
        List<Intersection> pointsDePassage = new ArrayList<>(
                this.tableauLivraison.getItems().size() + 1);
        pointsDePassage.add(this.plan.getEntrepot());
        for (Livraison pointDeLivraison : this.tableauLivraison.getItems()) {
            pointsDePassage.add(pointDeLivraison.getAdresse());
        }
        int nbSommetsDansGrapheComplet = pointsDePassage.size();

        // Graphe complet utilisé pour le TSP
        Graphe grapheComplet = new Graphe();
        for (Intersection intersection : pointsDePassage) {
            grapheComplet.ajouterSommet(new Sommet(intersection.getId()));
        }

        // Calcul de distance entre chaque adresse de livraison pour la
        // création d'un graphe complet
        // TODO créer et stocker les itinéraires (voir la feuille de François
        // pour la structure de données)
        // itineraireMap stocke l'itinéraire entre intersection1
        // vers intersection2
        // key est de la sorte "intersection1.id_intersection2.id"

        Map<String, Itineraire> itineraireMap = new HashMap<>();
        for (Intersection addresseLivraisonCourante : pointsDePassage) {
            // On calcul la distance entre l'adresse de livraison courante et
            // les autres adresse de livraisons
            Sommet sommetSource = graphe.getSommets().get(addresseLivraisonCourante.getId()); // adresse de livraison courante
            // Calcul des plus courts chemins (pcc)
            graphe = Graphe.calculerCheminplusCourtDepuisSource(graphe, sommetSource); // contient les pcc entre le sommet source et les autres sommets

            // Récupération des pcc pour créer le graphe complet et construire les intinéraires correspondants.
            for (Intersection adresseLivraison : pointsDePassage) {
                // TODO supprimer les arcs entre une livraison à 9h10h et une à 8h9h. Voir feuille François
                if (adresseLivraison.getId() != addresseLivraisonCourante.getId()) {
                    // Sommet correspondant au point de passage dans le graphe dijktra
                    Sommet sommetLivraison = graphe.getSommets().get(adresseLivraison.getId());
                    // Création de l'itinéraire
                    List<Intersection> intersectionsItineraire = new ArrayList<>(sommetLivraison.getCheminPlusCourt().size());
                    List<Sommet> cheminAdresseLivraisonCouranteVersAdresseLivraison = sommetLivraison.getCheminPlusCourt();

                    // S'il n'y a pas de chemin plus court entre les 2, on n'ajotue pas
                    if (!cheminAdresseLivraisonCouranteVersAdresseLivraison.isEmpty()) {
                        for (Sommet sommet : cheminAdresseLivraisonCouranteVersAdresseLivraison) {
                            Intersection intersectionDuPcc = this.plan.getIntersections().get(sommet.getNom());
                            intersectionsItineraire.add(intersectionDuPcc);
                        }
                        intersectionsItineraire.add(adresseLivraison); // On doit ajouter l'intersection destination
                        itineraireMap.put(addresseLivraisonCourante.getId() + "_" + adresseLivraison.getId(), new Itineraire(intersectionsItineraire));

                        // Sommets dans le graph complet
                        // TODO Il y a un bug quand il n'y a pas d'itinéraire entre 2 sommets
                        // TODO Il faudrait un helper pour la traduction dijstra vers TSP
                        Sommet sommetSourceDansGrapheComplet = grapheComplet.getSommets().get(sommetSource.getNom());
                        Sommet sommetDestDansGrapheComplet = grapheComplet.getSommets().get(sommetLivraison.getNom());
                        sommetSourceDansGrapheComplet.addDestination(sommetDestDansGrapheComplet,
                                sommetLivraison.getDistance());
                    }
                }
            }
            // réinitialisation des sommets du graphe pour le nouvel appel
            graphe.reinitialiserSommetsGraphe();
        }

        // DEBUG affichage des itinéraires de toute les livraison
        // TODO Créer la tournée
        Graph g = new CompleteGraph(grapheComplet);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);
        for (int i = 0; i < nbSommetsDansGrapheComplet; i++)
            System.out.print(tsp.getSolution(i) + " ");

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
        } catch (Exception e) {
            this.messageErreur.setText("Problème lors du chargement du plan.");
        }
    }

    private void afficherPlan(final Plan plan) {
        this.calquePlan = new CalquePlan();

        // intersections
        for (Intersection intersection : plan.getIntersections().values()) {
            Circle cercle = new Circle(3, Color.GREY);
            cercle.setVisible(false);
            calquePlan.ajouterPoint(intersection, cercle);
        }

        calquePlan.getPoints().forEach(point -> {
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
        calquePlan.ajouterPoint(entrepot, new Circle(5, Color.RED));

        // config carte
        MapView carteVue = new MapView();

        carteVue.setZoom(14.5);
        carteVue.flyTo(0, entrepot, 0.1); // centre la carte sur l'entrepot
        carteVue.addLayer(calquePlan); // ajout du calque contenant les points
        carteVue.setPrefHeight(carte.getHeight());
        carteVue.setPrefWidth(carte.getWidth());

        carteVue.setOnMouseClicked(e -> {
            Pair<Intersection, Circle> minPoint = calquePlan.trouverPointPlusProche(e.getX(), e.getY());
            Circle circle = minPoint.getValue();
            circle.setFill(Color.GREEN);
            circle.setVisible(true);
        });

        this.carte.getChildren().add(carteVue);
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public void afficherPointsDeLivraison(ActionEvent actionEvent) {
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        this.calquePlan.afficherPoints(checkBox.isSelected());
    }
}
