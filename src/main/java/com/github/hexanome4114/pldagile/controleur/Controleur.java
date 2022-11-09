package com.github.hexanome4114.pldagile.controleur;

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
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {

    /** Vitesse de déplacement d'un livreur. */
    public static final int VITESSE_MOYENNE = 15;

    /** Nombre de livreurs disponibles par défaut. */
    private static final int NOMBRE_LIVREURS = 1;

    private List<Livraison> livraisons;

    private List<Livreur> livreurs;

    private List<FenetreDeLivraison> fenetresDeLivraison;

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
                // bouton cliquable que lorsqu'une livraison est sélectionnée
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
            this.afficherPlan(plan);
        } catch (DocumentException e) {
            this.messageErreur.setText("Problème lors du chargement du plan.");
        }
    }

    private void afficherPlan(final Plan plan) {
        CalquePlan calque = new CalquePlan();

        // segments
        for (Segment segment : plan.getSegments()) {
            Intersection debut = segment.getDebut();
            Intersection fin = segment.getFin();

            calque.ajouterPoint(debut, new Circle(3, Color.BLUE));
            calque.ajouterPoint(fin, new Circle(3, Color.BLUE));
        }

        // entrepot
        Intersection entrepot = plan.getEntrepot();
        calque.ajouterPoint(entrepot, new Circle(5, Color.RED));

        // config carte
        MapView carteVue = new MapView();

        carteVue.setZoom(14.5);
        carteVue.flyTo(0, entrepot, 0.1); // centre la carte sur l'entrepot
        carteVue.addLayer(calque); // ajout du calque contenant les points

        calque.getPoints().forEach(point -> {
            // ajout d'un listener sur chaque point du calque
            point.getValue().setOnMouseClicked(e -> {
                e.consume();
                this.comboBoxAdresse.setValue(point.getKey());
            });
        });

        StackPane sp = new StackPane();
        sp.setPrefSize(carte.getPrefWidth(), carte.getPrefHeight());
        sp.getChildren().add(carteVue);

        this.carte.getChildren().add(sp);
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }
}
