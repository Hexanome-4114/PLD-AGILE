package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.algorithme.tsp.CompleteGraph;
import com.github.hexanome4114.pldagile.algorithme.tsp.Graph;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP1;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Itineraire;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.utilitaire.CalquePlan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import com.gluonhq.maps.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {

    private List<Livreur> livreurs;

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

        ObservableList<FenetreDeLivraison> oListFenetreDeLivraison =
                FXCollections.observableArrayList(FenetreDeLivraison.values());
        ObservableList<Livreur> oListLivreurs =
                FXCollections.observableArrayList(Livreur.values());

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
            this.calquePlan.ajouterLivraison(livraison);
            this.messageErreur.setText("");
        } else {
            this.messageErreur.setText("Veuillez renseigner tous les champs !");
        }
    }

    public void supprimerLivraison() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();
        this.tableauLivraison.getItems().remove(livraison);
        this.calquePlan.enleverLivraison(livraison);
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
            this.plan = Serialiseur.chargerPlan(fichier);
            this.afficherPlan(plan);
        } catch (Exception e) {
            this.messageErreur.setText("Problème lors du chargement du plan.");
        }
    }

    private void afficherPlan(final Plan plan) {
        this.calquePlan = new CalquePlan();

        // intersections
        for (Intersection intersection : plan.getIntersections().values()) {
            calquePlan.ajouterPoint(intersection);
        }

        // entrepot
        Intersection entrepot = plan.getEntrepot();
        calquePlan.setEntrepot(entrepot);

        // config carte
        MapView carteVue = new MapView();

        carteVue.setZoom(14.5);
        carteVue.flyTo(0, entrepot, 0.1); // centre la carte sur l'entrepot
        carteVue.addLayer(calquePlan); // ajout du calque contenant les points
        carteVue.setPrefSize(carte.getWidth(), carte.getHeight());
        carteVue.setCursor(Cursor.CROSSHAIR);

        carteVue.setOnMouseClicked(e -> {
            Intersection point = calquePlan.
                    trouverPointPlusProche(e.getX(), e.getY());

            this.calquePlan.setPointSelectionne(point);
            this.comboBoxAdresse.setValue(point);
        });

        this.carte.getChildren().add(carteVue);
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public void afficherPointsDeLivraison(final ActionEvent actionEvent) {
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        this.calquePlan.afficherPoints(checkBox.isSelected());
    }
}
