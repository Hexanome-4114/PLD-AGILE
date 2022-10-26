package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.modele.Segment;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.DocumentException;

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

    /** Vue de l'application. */
    private Stage stage;

    @FXML
    private Pane carte;

    @FXML
    private ComboBox<Livreur> comboBoxLivreur;

    @FXML
    private ComboBox<FenetreDeLivraison> comboBoxFenetreDeLivraison;

    @FXML
    private TableView<Livraison> tableauLivraison;

    @FXML
    private TableColumn<Livraison, Integer> numeroLivraison;

    @FXML
    private TableColumn<Livraison, Livreur> livreur;

    @FXML
    private TableColumn<Livraison, FenetreDeLivraison> fenetreDeLivraison;

    @FXML
    public void initialize() {
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

    public void ajouterLivraison() {
        /* TODO vérifier que le livreur, la fenêtre et l'adresse
            ne sont pas vides avant de créer l'objet = obliger
            l'utilisateur à rentrer les infos */
        Livraison livraison = new Livraison(
                this.livraisons.size() + 1,
                this.comboBoxFenetreDeLivraison.getValue(),
                this.comboBoxLivreur.getValue(),
                new Intersection(4.857418, 45.75406));
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

        this.afficherPlan(plan);
    }

    private void afficherPlan(final Plan plan) {
        // segments
        for (Segment segment : plan.getSegments()) {
            Intersection debut = segment.getDebut();
            Intersection fin = segment.getFin();

            Line ligne = new Line(
                    debut.getX(), debut.getY(), fin.getX(), fin.getY()
            );

            this.carte.getChildren().add(ligne);
        }

        // entrepot
        Circle entrepot = new Circle();

        entrepot.setCenterX(plan.getEntrepot().getX());
        entrepot.setCenterY(plan.getEntrepot().getY());
        entrepot.setRadius(5);
        entrepot.setFill(Color.RED);

        this.carte.getChildren().add(entrepot);
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }
}
