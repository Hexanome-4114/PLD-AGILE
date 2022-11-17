package com.github.hexanome4114.pldagile.controleur;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contrôleur de l'application.
 */
public final class Controleur {
    /**
     * Temps passé pour effectuer chaque livraison.
     */
    public static final int TEMPS_PAR_LIVRAISON = 5;

    private Plan plan;

    private List<Tournee> tournees;

    private List<Livraison> livraisons;

    private CalquePlan calquePlan;

    private ListDeCommandes listeDeCommandes;

    private Etat etatCourant;

    /** Instances associées aux différents états du controleur. */
    private final EtatInitial etatInitial = new EtatInitial();
    private final EtatPlanCharge etatPlanCharge = new EtatPlanCharge();
    private final EtatLivraison etatLivraison = new EtatLivraison();
    private final EtatTournee etatTournee = new EtatTournee();

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
    private Button genererFeuillesDeRouteBouton;

    @FXML
    private CheckBox afficherPointsCheckBox;

    @FXML
    private CheckBox afficherLivreursCheckBox;

    @FXML
    private CheckBox afficherLivreur1CheckBox;

    @FXML
    private CheckBox afficherLivreur2CheckBox;

    @FXML
    private CheckBox afficherLivreur3CheckBox;

    @FXML
    private CheckBox afficherLivreur4CheckBox;

    @FXML
    public void initialize() {
        System.setProperty("javafx.platform", "desktop");

        this.livraisons = new ArrayList<>();
        this.listeDeCommandes = new ListDeCommandes();

        ObservableList<FenetreDeLivraison> oListFenetreDeLivraison =
                FXCollections.observableArrayList(FenetreDeLivraison.values());
        ObservableList<Livreur> oListLivreurs =
                FXCollections.observableArrayList(Livreur.values());

        this.comboBoxLivreur.setItems(oListLivreurs);
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

        this.afficherLivreur1CheckBox.setUserData(Livreur.LIVREUR_1);
        this.afficherLivreur2CheckBox.setUserData(Livreur.LIVREUR_2);
        this.afficherLivreur3CheckBox.setUserData(Livreur.LIVREUR_3);
        this.afficherLivreur4CheckBox.setUserData(Livreur.LIVREUR_4);

        // EventListener de la vue
        this.tableauLivraison.getSelectionModel().selectedItemProperty()
                // bouton cliquable que lorsqu'une livraison est sélectionnée
                .addListener((obs, ancienneSelection, nouvelleSelection) -> {

                            this.supprimerLivraisonBouton.setDisable(
                                    nouvelleSelection == null);
                            this.calquePlan.setLivraisonSelectionnee(
                                    nouvelleSelection);
                        }
                );
        this.tableauLivraison.getItems()
                // bouton cliquable que lorsqu'il y a des livraisons
                .addListener((ListChangeListener<Livraison>) (obs) -> {
                            boolean tableauEstVide = obs.getList().isEmpty();

                            this.sauvegarderLivraisonsBouton.setDisable(
                                    tableauEstVide);
                            this.calculerTourneeBouton.setDisable(
                                    tableauEstVide);
                            this.annulerBouton.setDisable(
                                    listeDeCommandes.getIndexCourant() == -1);
                            this.tableauLivraison.setDisable(tableauEstVide);
                            this.afficherLivreursCheckBox.setDisable(
                                    livraisons.isEmpty());
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
        int numero;

        if (livraisons.isEmpty()) {
            numero = 1;
        } else { // le numéro de la dernière livraison + 1
            numero = livraisons.get(livraisons.size() - 1).getNumero() + 1;
        }

        Livraison livraison = new Livraison(
                numero,
                this.comboBoxFenetreDeLivraison.getValue(),
                this.comboBoxLivreur.getValue(),
                this.comboBoxAdresse.getValue()
        );

        this.reinitialiserPointSelectionne();
        this.listeDeCommandes.ajouter(new AjouterCommande(this, livraison));
        this.etatCourant.ajouterLivraison(this);
    }

    public void supprimerLivraison() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();

        this.listeDeCommandes.ajouter(
                new AnnulerCommande(new AjouterCommande(this, livraison))
        );
    }

public void supprimerLivraisonApresCalcul() {
        Livraison livraison = this.tableauLivraison.getSelectionModel()
                .getSelectedItem();

        this.listeDeCommandes.ajouter(
                new AnnulerCommande(new AjouterCommande(this, livraison))
        );

        for (Tournee tournee : this.tournees) {
            if (tournee.getLivreur().getNumero()
                    == livraison.getLivreur().getNumero()) {
                tournee.supprimerLivraisonApresCalcul(livraison);
            }
        }
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
            // on sauvegarde que les livraisons affichées dans le tableau
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
        if (!this.livraisons.isEmpty()) {
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
            List<Livraison> livraisons = Serialiseur.chargerLivraisons(
                    fichier, this.plan);

            this.reinitialiserLivraisons();
            this.reinitialiserPointSelectionne();

            for (Livraison livraison : livraisons) {
                this.ajouterLivraison(livraison);
            }

            this.etatCourant.chargerLivraison(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.afficherPopUp(
                    "Problème lors du chargement des livraisons.",
                    Alert.AlertType.ERROR
            );
        }
    }

    public void calculerLesTournees() {
        // Pour chaque livreur, on appelle "calculerTournee" pour calculer
        // la tournée qui lui est associée
        this.tournees = new ArrayList<>();
        for (Livreur livreur : this.comboBoxLivreur.getItems()) {

            // on récupère les livraisons du livreur courant
            List<Livraison> livraisons = this.livraisons
                    .stream().filter(
                            livraison -> livraison.getLivreur().equals(livreur))
                    .collect(Collectors.toList());

            // On ne crée pas de tournée s'il n'y a pas de livraison
            // pour un livreur
            if (!livraisons.isEmpty()) {
                Tournee tournee = new Tournee(livreur, livraisons, this.plan,
                        TEMPS_PAR_LIVRAISON, this.plan.getEntrepot());
                tournee.calculerTournee(FenetreDeLivraison.H8_H9);

                boolean manqueItineraire = tournee.getItineraires() == null;
                if (!manqueItineraire) {
                    for (Itineraire itineraire : tournee.getItineraires()) {
                        if (itineraire == null) {
                            manqueItineraire = true;
                            break;
                        }
                    }
                }
                if (manqueItineraire) {
                    this.afficherPopUp(
                            "Aucun itinéraire possible pour cette tournée.",
                            Alert.AlertType.ERROR
                    );
                } else {
                    this.tournees.add(tournee);
                    afficherTournee(tournee);
                    this.etatCourant.calculerTournee(this);
                }
            }
        }
    }

    public void genererFeuillesDeRoute() {
        DirectoryChooser selecteurDossier = new DirectoryChooser();
        selecteurDossier.setTitle("Sélectionner un dossier vide où enregistrer les feuilles de routes.");

        // TODO dossier vide
        File dossier = selecteurDossier.showDialog(this.stage);

        if (dossier == null) { // aucun dossier sélectionné
            return;
        }

        try {
            List<CheckBox> checkBoxList = new ArrayList<>();
            checkBoxList.add(this.afficherLivreur1CheckBox);
            checkBoxList.add(this.afficherLivreur2CheckBox);
            checkBoxList.add(this.afficherLivreur3CheckBox);
            checkBoxList.add(this.afficherLivreur4CheckBox);
            for(CheckBox checkBox : checkBoxList) {
                Livreur livreur = (Livreur) checkBox.getUserData();
                Tournee tournee = null;
                for(Tournee tournee1 : this.tournees) {
                    if(tournee1.getLivreur().equals(livreur)) {
                        tournee = tournee1;
                        break;
                    }
                }
                if (tournee != null) {
                    File feuilleDeRoute = new File(dossier.getPath() + "/feuille_de_route_" + livreur.getNumero() + ".txt");
                    Serialiseur.genererFeuilleDeRoute(feuilleDeRoute, tournee);
                }
            }
        } catch (Exception e) {
            this.afficherPopUp(
                    "Problème lors de la sauvegarde des livraisons.",
                    Alert.AlertType.ERROR
            );
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
            this.etatCourant.chargerPlan(this);
            this.afficherPlan(plan);
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

            // s'il n'y a pas de sélection en cours ou que l'adresse de celle-ci
            // est différente du point le plus proche
            if (calquePlan.getLivraisonSelectionnee() == null
                    || !point.equals(calquePlan.getLivraisonSelectionnee()
                    .getAdresse())) {
                this.calquePlan.setPointSelectionne(point);
                this.comboBoxAdresse.setValue(point);
            } else {
                this.calquePlan.setPointSelectionne(null);
                this.comboBoxAdresse.setValue(null);
            }
        });

        this.carte.getChildren().clear();
        this.carte.getChildren().add(carteVue);
    }

    public void afficherPointsDeLivraison(final ActionEvent actionEvent) {
        this.afficherPointsCheckBox = (CheckBox) actionEvent.getSource();
        this.calquePlan.afficherPoints(
                this.afficherPointsCheckBox.isSelected()
        );
    }

    public void afficherDonneesLivreurs(final ActionEvent actionEvent) {
        CheckBox checkBox = (CheckBox) actionEvent.getSource();

        CheckBox[] checkBoxes = {
                afficherLivreur1CheckBox, afficherLivreur2CheckBox,
                afficherLivreur3CheckBox, afficherLivreur4CheckBox};

        // gestion des états des checkBoxes
        if (this.afficherLivreursCheckBox.isSelected()) {
            for (CheckBox c : checkBoxes) {
                c.setSelected(true);
                c.setDisable(true);
            }
        } else if (checkBox == this.afficherLivreursCheckBox) {
            for (CheckBox c : checkBoxes) {
                c.setSelected(true);
                c.setDisable(false);
            }
        }

        // mise à jour du calque et du tableau
        List<Livreur> livreurs = getLivreursSelectionnes();

        this.calquePlan.afficherDonneesLivreurs(livreurs);
        this.tableauLivraison.getItems().clear();

        // on affiche toutes les livraisons
        if (livreurs.size() == Livreur.values().length) {
            this.tableauLivraison.getItems().addAll(
                    FXCollections.observableArrayList(this.livraisons));
        } else { // on filtre les livraisons selon les livreurs sélectionnés
            this.tableauLivraison.getItems().addAll(FXCollections
                    .observableArrayList(this.livraisons.stream().filter(
                            livraison -> livreurs.contains(livraison
                                    .getLivreur())).collect(Collectors.toList()
                    )));
        }
    }

    /**
     * Retourne les livreurs dont la checkbox est sélectionnée.
     * @return les livreurs dont la checkbox est sélectionnée.
     */
    private List<Livreur> getLivreursSelectionnes() {
        if (afficherLivreursCheckBox.isSelected()) {
            return Arrays.asList(Livreur.values());
        }

        List<Livreur> livreurs = new ArrayList<>();

        CheckBox[] checkBoxes = {
                afficherLivreur1CheckBox, afficherLivreur2CheckBox,
                afficherLivreur3CheckBox, afficherLivreur4CheckBox};

        for (CheckBox c : checkBoxes) {
            if (c.isSelected()) {
                livreurs.add((Livreur) c.getUserData());
            }
        }

        return livreurs;
    }

    private void afficherTournee(final Tournee tournee) {
        for (Itineraire itineraire : tournee.getItineraires()) {
            for (int j = 1; j < itineraire.getIntersections().size(); j++) {
                this.calquePlan.ajouterSegment(
                        itineraire.getIntersections().get(j - 1),
                        itineraire.getIntersections().get(j),
                        tournee.getLivreur());
            }
        }
    }

    private void supprimerAffichageTournee(final Tournee tournee) {
        for (Itineraire itineraire : tournee.getItineraires()) {
            for (int j = 1; j < itineraire.getIntersections().size(); j++) {
                this.calquePlan.supprimerSegment(
                        itineraire.getIntersections().get(j - 1),
                        itineraire.getIntersections().get(j),
                        tournee.getLivreur());
            }
        }
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
        // si la checkbox du livreur n'est pas sélectionnée, on ajoute la
        // livraison mais on ne l'affiche pas sur la carte

        this.livraisons.add(l);

        boolean afficherLivraison = this.getLivreursSelectionnes()
                .contains(l.getLivreur());

        if (afficherLivraison) {
            this.tableauLivraison.getItems().add(l);
        }

        Node noeud = this.calquePlan.ajouterLivraison(l, afficherLivraison);

        noeud.setOnMouseClicked(e -> {
            this.tableauLivraison.getSelectionModel().select(l);
        });
    }

    public void supprimerLivraison(final Livraison l) {
        this.livraisons.remove(l);
        this.tableauLivraison.getItems().remove(l);
        this.calquePlan.enleverLivraison(l);
    }

    public void reinitialiserLivraisons() {
        for (Livraison livraison : this.tableauLivraison.getItems()) {
            this.calquePlan.enleverLivraison(livraison);
        }
        this.livraisons.clear();
        this.tableauLivraison.getItems().clear();
    }

    private void reinitialiserPointSelectionne() {
        this.calquePlan.setPointSelectionne(null);
        this.comboBoxAdresse.setValue(null);
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

    public EtatTournee getEtatTournee() {
        return etatTournee;
    }

    public ListDeCommandes getListeDeCommandes() {
        return this.listeDeCommandes;
    }

    public CheckBox getAfficherPointsCheckBox() {
        return this.afficherPointsCheckBox;
    }

    public CheckBox getAfficherLivreursCheckBox() {
        return this.afficherLivreursCheckBox;
    }

    public CheckBox getAfficherLivreur1CheckBox() {
        return this.afficherLivreur1CheckBox;
    }

    public CheckBox getAfficherLivreur2CheckBox() {
        return this.afficherLivreur2CheckBox;
    }

    public CheckBox getAfficherLivreur3CheckBox() {
        return this.afficherLivreur3CheckBox;
    }

    public CheckBox getAfficherLivreur4CheckBox() {
        return this.afficherLivreur4CheckBox;
    }

    public Button getGenererFeuillesDeRouteBouton() {
        return this.genererFeuillesDeRouteBouton;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public void setEtatCourant(final Etat etat) {
        this.etatCourant = etat;
    }
}
