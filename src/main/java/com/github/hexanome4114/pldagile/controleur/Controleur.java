package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.modele.Segment;
import com.github.hexanome4114.pldagile.utilitaire.CalquePlan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import com.gluonhq.maps.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import org.dom4j.DocumentException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {

    /** Vitesse de déplacement d'un livreur. */
    private static final int VITESSE_MOYENNE = 15;

    /** Nombre de livreurs disponibles par défaut. */
    private static final int NOMBRE_LIVREURS = 1;

    private List<Livraison> livraisons;

    private List<Livreur> livreurs;

    private List<FenetreDeLivraison> fenetresDeLivraison;

    private Circle pointClique;
    private Plan plan;

    /** Vue de l'application. */
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
    public void initialize() {
        System.setProperty("javafx.platform", "desktop");

        this.livraisons = new ArrayList<>();
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
            Livraison livraison = new Livraison(
                    this.livraisons.size() + 1,
                    this.comboBoxFenetreDeLivraison.getValue(),
                    this.comboBoxLivreur.getValue(),
                    this.comboBoxAdresse.getValue());
            this.livraisons.add(livraison);

            this.numeroLivraison.setCellValueFactory(
                    new PropertyValueFactory<>("numero"));
            this.livreur.setCellValueFactory(
                    new PropertyValueFactory<>("livreur"));
            this.fenetreDeLivraison.setCellValueFactory(
                    new PropertyValueFactory<>("fenetreDeLivraison"));

            ObservableList<Livraison> oListLivraison =
                    FXCollections.observableArrayList(this.livraisons);

            this.tableauLivraison.setItems(oListLivraison);
            this.messageErreur.setText("");
        } else {
            this.messageErreur.setText("Veuillez renseigner tous les champs !");
        }
    }

    public void calculerTournee() {

        // Dijkstra
        // Creation de chaque noeud et ajout dans le graphe
        Graphe graphe = new Graphe();
        for (Map.Entry<String,Intersection> intersection : this.plan.getIntersections().entrySet()) {
            Sommet sommet = new Sommet(intersection.getKey());
            graphe.ajouterSommet(sommet);
        }

        // ajout des sommets adjacents et de la distance grâce aux segments
        for (Segment segment: this.plan.getSegments()) {
            Intersection intersectionDebut = segment.getDebut();
            Intersection intersectionFin = segment.getFin();
            Sommet sommetOrigine = graphe.getSommets().get(intersectionDebut.getId());
            Sommet sommetDestination = graphe.getSommets().get(intersectionFin.getId());
            sommetOrigine.addDestination(sommetDestination, segment.getLongueur());
        }

        // TODO : faire une boucle sur toutes les livraisons pour calculer Dijkstra depuis chacune des adresses de livraisons
        Intersection sommentIntersection = this.livraisons.get(0).getAdresse();
        Sommet sommetSource = graphe.getSommets().get(sommentIntersection.getId());
        graphe = Graphe.calculerCheminplusCourtDepuisSource(graphe, sommetSource);
        System.out.println(graphe.toString());

    }


    public void supprimerLivraison(final Livraison livraison) {
        this.livraisons.remove(livraison);
    }

    @FXML
    private void chargerPlan() {
        FileChooser selecteurFichier = new FileChooser();
        selecteurFichier.setTitle("Sélectionnez un fichier xml");
        selecteurFichier.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
        );

        File fichier = selecteurFichier.showOpenDialog(this.stage);

        if (fichier == null) { // aucun fichier sélectionné
            return;
        }

        URL url;
        Plan plan;

        try {
            url = Path.of(fichier.getPath()).toUri().toURL();
            plan = Serialiseur.chargerPlan(url);
        } catch (MalformedURLException | DocumentException e) {
            throw new RuntimeException(e); // TODO gérer les exceptions
        }

        this.plan = plan;
        this.afficherPlan(plan);
    }

    private void afficherPlan(final Plan plan) {
        CalquePlan calque = new CalquePlan();

        // segments
        for (Segment segment : plan.getSegments()) {
            Intersection debut = segment.getDebut();
            Intersection fin = segment.getFin();

            calque.ajouterPoint(debut, new Circle(3, Color.GREY));
            calque.ajouterPoint(fin, new Circle(3, Color.GREY));
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
