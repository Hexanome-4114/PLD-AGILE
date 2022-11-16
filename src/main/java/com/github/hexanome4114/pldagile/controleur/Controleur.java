package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.algorithme.dijkstra.Graphe;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.Sommet;
import com.github.hexanome4114.pldagile.algorithme.tsp.CompleteGraph;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP;
import com.github.hexanome4114.pldagile.algorithme.tsp.TSP1;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Itineraire;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.modele.Tournee;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {

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
    private TableColumn<Livraison, Integer> numeroLivraisonColonne;

    @FXML
    private TableColumn<Livraison, Livreur> livreurColonne;

    @FXML
    private TableColumn<Livraison, FenetreDeLivraison>
            fenetreDeLivraisonColonne;

    @FXML
    private Label instructionLabel;

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

        ObservableList<FenetreDeLivraison> oListFenetreDeLivraison =
                FXCollections.observableArrayList(FenetreDeLivraison.values());
        ObservableList<Livreur> oListLivreurs =
                FXCollections.observableArrayList(Livreur.values());

        this.comboBoxLivreur.setPromptText("Livreur");
        this.comboBoxLivreur.setItems(oListLivreurs);
        this.comboBoxFenetreDeLivraison.setPromptText("Fenêtre de livraison");
        this.comboBoxFenetreDeLivraison.setItems(oListFenetreDeLivraison);
        Image annulerImage = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(
                        "/images/annuler.png")
                ), 18, 18, false, false
        );
        this.annulerBouton.setGraphic(new ImageView(annulerImage));
        this.etatCourant = etatInitial;

        this.numeroLivraisonColonne.setCellValueFactory(
                new PropertyValueFactory<>("numero"));
        this.livreurColonne.setCellValueFactory(
                new PropertyValueFactory<>("livreur"));
        this.fenetreDeLivraisonColonne.setCellValueFactory(
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

            if (this.verificationDoublonLivraison(livraison)) {
                this.afficherPopUp(
                        "Une livraison existe déjà à cette adresse.",
                        Alert.AlertType.ERROR
                );
            } else {
                this.listeDeCommandes.ajouter(
                        new AjouterCommande(this, livraison)
                );
                this.etatCourant.ajouterLivraison(this);
                this.calquePlan.ajouterLivraison(livraison);
            }
        }
    }

    public void supprimerLivraison() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();

        this.listeDeCommandes.ajouter(
                new AnnulerCommande(new AjouterCommande(this, livraison))
        );
        this.calquePlan.enleverLivraison(livraison);
    }

    public void annuler() {
        this.listeDeCommandes.annuler();
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
            this.afficherPopUp(
                    "Problème lors de la sauvegarde des livraisons.",
                    Alert.AlertType.ERROR
            );
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
            // Vérification si toutes les adresses de livraisons sont sur
            // le plan et qu'il n'y ait pas de livraisons en double.
            for (Livraison l : livraisons) {
                if (!this.plan.getIntersections().containsKey(
                        l.getAdresse().getId())
                ) {
                        throw new Exception();
                }
                if (this.verificationDoublonLivraison(l)) {
                    throw new Exception();
                }
            }
            this.tableauLivraison.setItems(
                    FXCollections.observableArrayList(livraisons));
            this.etatCourant.ajouterLivraison(this);
        } catch (Exception e) {
            this.afficherPopUp(
                    "Problème lors du chargement des livraisons.",
                    Alert.AlertType.ERROR
            );
        }
    }

    public List<Tournee> calculerLesTournees() {
        List<Tournee> tournees = new ArrayList<>();
        // Pour chaque livreur, on appelle "calculerTournee"
        // pour calcule la tournée qui lui est associé
        for (Livreur livreur : Livreur.values()) {
            // on récupère les livraisons du livreur courant
            List<Livraison> livraisons = this.tableauLivraison
                    .getItems().stream().filter(
                            livraison -> livraison.getLivreur() == livreur)
                    .collect(Collectors.toList()
                    );
            Tournee tournee = calculerTournee(
                    livreur,
                    livraisons,
                    this.plan.getEntrepot(),
                    FenetreDeLivraison.H8_H9
            );
            tournees.add(tournee);
            afficherTournee(tournee);
        }

        return tournees;
    }

    /**
     * Calcule une Tournée qui part et revient sur la première livraison,
     * le livreur partira à l'heure de la première livraison.
     *
     * @param livreur livreur qui effectue les livraisons
     * @param livraisons liste des livraisons à effectuer, la première livraison est le point de départ et d'arrivée.
     * @param pointDepart intersection de départ et d'arrivée de la tournée (doit être différents de la premiere livraison)
     * @param fdlDepart fenetre de livraison de départ
     * @return Tournée
     */
    public Tournee calculerTournee(final Livreur livreur,
                                   final List<Livraison> livraisons,
                                   final Intersection pointDepart,
                                   final FenetreDeLivraison fdlDepart) {
        // Dijkstra
        // Creation de chaque sommet et ajout dans le graphe
        Graphe graphe = ControleurHelper.creerGrapheDijkstra(
                this.plan.getIntersections().keySet()
        );

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

        // Ajout d'une livraison factice au point de depart
        Livraison livraisonPointDepart = new Livraison(
                Integer.MAX_VALUE,
                fdlDepart,
                livreur,
                pointDepart
        );
        List<Livraison> livraisonTemporaire = new ArrayList<>(livraisons);
        livraisonTemporaire.add(0, livraisonPointDepart);

        // Création du graphe et calcul de tous les
        // itinéraires nécessaires pour l'affichage
        Pair<Graphe, Map<String, Itineraire>> resultCreationGrapheTSP
                = ControleurHelper.creerGrapheTSP(
                        livraisonTemporaire,
                        this.plan.getIntersections(),
                        graphe
        );
        Graphe grapheTSP = resultCreationGrapheTSP.getKey();
        Map<String, Itineraire> itineraireMap
                = resultCreationGrapheTSP.getValue();

        int nbSommetsDansGrapheTSP = livraisonTemporaire.size();

        CompleteGraph g = ControleurHelper.convertirGrapheVersCompleteGraph(
                grapheTSP
        );
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        // On recree une liste de livraisons pour reordonner
        List<Livraison> livraisonsTSP = new ArrayList<>(livraisons.size());
        List<Itineraire> itinerairesFinaux = new ArrayList<>();
        // S'il il existe une solution
        if (tsp.getSolutionCost() != Integer.MAX_VALUE) {
            // On garde uniquement les itinéraires
            // dont nous avons besoin et on crée la tournée à renvoyer.
            for (int i = 0; i < nbSommetsDansGrapheTSP - 1; ++i) {
                String idSommet = g.getMapIndexVersNomSommet().get(
                        tsp.getSolution(i)
                );
                String idSommet1 = g.getMapIndexVersNomSommet().get(
                        tsp.getSolution(i + 1)
                );
                Itineraire itineraire = itineraireMap.get(
                        idSommet + "_" + idSommet1
                );
                itinerairesFinaux.add(itineraire);
                // Ajoute la livraison dans l'ordre de passage
                for (Livraison livraison : livraisons) {
                    // on n'ajoute pas la livraison
                    // factice 'livraisonPointDepart'
                    if (livraison.getAdresse().getId() == idSommet1) {
                        livraisonsTSP.add(livraison);
                        break;
                    }
                }
            }
            // Ajout du dernier itinéraire entre la dernière adresse
            // de livraison visité et la première (l'entrepôt)
            String idPremierSommet = g.getMapIndexVersNomSommet().get(0);
            String idDernierSommet = g.getMapIndexVersNomSommet().get(
                    tsp.getSolution(nbSommetsDansGrapheTSP - 1)
            );
            itinerairesFinaux.add(itineraireMap.get(
                    idDernierSommet + "_" + idPremierSommet)
            );
        }

        Tournee tournee = new Tournee(
                livreur,
                livraisonsTSP,
                itinerairesFinaux
        );

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
            this.plan = Serialiseur.chargerPlan(fichier);
            this.afficherPlan(plan);
            this.etatCourant.chargerPlan(this);
        } catch (Exception e) {
            this.afficherPopUp(
                    "Problème lors du chargement du plan.",
                    Alert.AlertType.ERROR
            );
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

    public void afficherPointsDeLivraison(final ActionEvent actionEvent) {
        this.afficherPointsCheckBox = (CheckBox) actionEvent.getSource();
        this.calquePlan.afficherPoints(
                this.afficherPointsCheckBox.isSelected()
        );
    }

    private void afficherTournee(final Tournee tournee) {
        for (Itineraire itineraire : tournee.getItineraires()) {
            for (int i = 1; i < itineraire.getIntersections().size(); i++) {
                this.calquePlan.ajouterSegment(
                        itineraire.getIntersections().get(i - 1),
                        itineraire.getIntersections().get(i));
            }
        }
    }

    private boolean verificationDoublonLivraison(final Livraison livraison) {
        boolean doublon = false;
        for (Livraison l : this.tableauLivraison.getItems()) {
            if (l.getAdresse().getId().equals(
                    livraison.getAdresse().getId())
            ) {
                doublon = true;
                break;
            }
        }
        return doublon;
    }

    private void afficherPopUp(
            final String message,
            final Alert.AlertType type
    ) {
        Alert alerte = new Alert(type);
        alerte.setHeaderText(message);
        alerte.show();
    }

    public void ajouterLivraison(final Livraison l) {
        this.tableauLivraison.getItems().add(l);
    }

    public void supprimerLivraison(final Livraison l) {
        this.tableauLivraison.getItems().remove(l);
    }

    public Label getInstructionLabel() {
        return this.instructionLabel;
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

    public CalquePlan getCalquePlan() {
        return calquePlan;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public void setEtatCourant(final Etat etat) {
        this.etatCourant = etat;
    }
}
