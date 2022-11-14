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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableArray;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;

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

    private Plan plan;

    private CalquePlan calquePlan;

    private ListDeCommandes listeDeCommandes;

    private Etat etatCourant;

    /** Instances associées aux différents états du controleur. */
    private final EtatInitial etatInitial = new EtatInitial();
    private final EtatPlanCharge etatPlanCharge = new EtatPlanCharge();
    private final EtatLivraison etatLivraison = new EtatLivraison();

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
    private Button annulerBouton;

    @FXML
    private Button sauvegarderLivraisonsBouton;

    @FXML
    private Button ajouterLivraisonBouton;

    @FXML
    private Button chargerLivraisonBouton;

    @FXML
    private Button chargerPlanBouton;

    @FXML
    private Button calculerTourneeBouton;

    @FXML
    private CheckBox afficherPointsCheckBox;

    @FXML
    public void initialize() {
        System.setProperty("javafx.platform", "desktop");

        this.listeDeCommandes = new ListDeCommandes();

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
        Image annulerImage = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(
                        "/images/annuler.png")
                )
        );
        this.annulerBouton.setGraphic(new ImageView(annulerImage));
        this.etatCourant = etatInitial;

        this.numeroLivraison.setCellValueFactory(
                new PropertyValueFactory<>("numero"));
        this.livreur.setCellValueFactory(
                new PropertyValueFactory<>("livreur"));
        this.fenetreDeLivraison.setCellValueFactory(
                new PropertyValueFactory<>("fenetreDeLivraison"));

        // EventListener de la vue
        this.tableauLivraison.getSelectionModel().selectedItemProperty()
                // bouton cliquable que lorsqu'une livraison est sélectionnée
                .addListener((obs, ancienneSelection, nouvelleSelection)
                        -> this.supprimerLivraisonBouton.setDisable(
                        nouvelleSelection == null)
                );
        this.tableauLivraison.getItems()
                // bouton cliquable que lorsqu'il y a des livraisons
                .addListener((ListChangeListener<Livraison>) (obs)
                                -> {
                            this.sauvegarderLivraisonsBouton.setDisable(
                                    obs.getList().isEmpty());
                            this.calculerTourneeBouton.setDisable(
                                    obs.getList().isEmpty());
                            this.getAnnulerBouton().setDisable(
                                    listeDeCommandes.getIndexCourant() == -1
                            );
                }
                );
        this.comboBoxLivreur.valueProperty().
                addListener((obs, ancienneSelection, nouvelleSelection) ->
                        this.ajouterLivraisonBouton.setDisable(
                        this.comboBoxLivreur.getValue() == null
                        || this.comboBoxFenetreDeLivraison.getValue() == null
                        || this.comboBoxAdresse.getValue() == null));
        this.comboBoxFenetreDeLivraison.valueProperty().
                addListener((options, oldValue, newValue) ->
                        this.ajouterLivraisonBouton.setDisable(
                        this.comboBoxLivreur.getValue() == null
                        || this.comboBoxFenetreDeLivraison.getValue() == null
                        || this.comboBoxAdresse.getValue() == null));
        this.comboBoxAdresse.valueProperty().
                addListener((options, oldValue, newValue) ->
                        this.ajouterLivraisonBouton.setDisable(
                        this.comboBoxLivreur.getValue() == null
                        || this.comboBoxFenetreDeLivraison.getValue() == null
                        || this.comboBoxAdresse.getValue() == null));
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

            this.listeDeCommandes.add(new AjouterCommande(this, livraison));

            this.etatCourant.ajouterLivraison(this);
        }
    }

    public void supprimerLivraison() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();

        this.listeDeCommandes.add(
                new AnnulerCommande(new AjouterCommande(this, livraison))
        );
    }

    public void annuler() {
        this.listeDeCommandes.undo();
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
            this.etatCourant.ajouterLivraison(this);
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
        for (int i = 0; i < nbSommetsDansGrapheComplet; i++) {
            System.out.print(tsp.getSolution(i) + " ");
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
            this.plan = Serialiseur.chargerPlan(fichier);
            this.afficherPlan(plan);
            this.etatCourant.chargerPlan(this);
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
            Pair<Intersection, Circle> point = calquePlan.
                    trouverPointPlusProche(e.getX(), e.getY());

            this.calquePlan.setPointSelectionne(point.getValue());
            this.comboBoxAdresse.setValue(point.getKey());
        });

        this.carte.getChildren().add(carteVue);
    }

    public void afficherPointsDeLivraison(final ActionEvent actionEvent) {
        this.afficherPointsCheckBox = (CheckBox) actionEvent.getSource();
        this.calquePlan.afficherPoints(
                this.afficherPointsCheckBox.isSelected()
        );
    }

    public void ajouterLivraison(final Livraison l) {
        this.tableauLivraison.getItems().add(l);
    }

    public void supprimerLivraison(final Livraison l) {
        this.tableauLivraison.getItems().remove(l);
    }

    public ComboBox<Livreur> getComboBoxLivreur() {
        return this.comboBoxLivreur;
    }

    public ComboBox<FenetreDeLivraison> getComboBoxFenetreDeLivraison() {
        return this.comboBoxFenetreDeLivraison;
    }

    public Button getAjouterLivraisonBouton() {
        return this.ajouterLivraisonBouton;
    }

    public Button getChargerPlanBouton() {
        return this.chargerPlanBouton;
    }

    public Button getAnnulerBouton() {
        return this.annulerBouton;
    }

    public Button getSauvegarderLivraisonsBouton() {
        return this.sauvegarderLivraisonsBouton;
    }

    public Button getSupprimerLivraisonBouton() {
        return this.supprimerLivraisonBouton;
    }

    public Button getChargerLivraisonBouton() {
        return this.chargerLivraisonBouton;
    }

    public Button getCalculerTourneeBouton() {
        return this.calculerTourneeBouton;
    }

    public TableView<Livraison> getTableauLivraison() {
        return this.tableauLivraison;
    }

    public ComboBox<Intersection> getComboBoxAdresse() {
        return this.comboBoxAdresse;
    }

    public EtatPlanCharge getEtatPlanCharge() {
        return this.etatPlanCharge;
    }

    public EtatLivraison getEtatLivraison() {
        return this.etatLivraison;
    }

    public ListDeCommandes getListeDeCommandes() {
        return this.listeDeCommandes;
    }

    public CheckBox getAfficherPointsCheckBox() {
        return this.afficherPointsCheckBox;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public void setEtatCourant(final Etat etat) {
        this.etatCourant = etat;
    }
}
