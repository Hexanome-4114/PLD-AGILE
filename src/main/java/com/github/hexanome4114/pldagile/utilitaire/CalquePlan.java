package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.gluonhq.maps.MapLayer;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private static final int TAILLE_POINT = 4;

    private static final Color COULEUR_POINT = Color.RED;

    private static final Color COULEUR_RETARD = Color.RED;

    private static final Image IMAGE_ENTREPOT = new Image(
            CalquePlan.class.getResource("/images/entrepot.png").toString(),
            25, 25, false, false);

    private static final Image IMAGE_POINT_SELECTIONNE = new Image(
            CalquePlan.class.getResource("/images/pin.png").toString());

    private static final int TAILLE_SEGMENT = 3;

    private static final int TAILLE_FLECHE = 5;

    private double zoomReference = -1.0;

    /**
     * Map contenant les points de livraison et leur Node associé.
     */
    private final ObservableMap<Intersection, Circle> points =
            FXCollections.observableHashMap();

    /**
     * Map contenant les livraisons et leur Node associé.
     */
    private final ObservableMap<Livraison, Node> livraisons =
            FXCollections.observableHashMap();


    /**
     * Map contenant les segments de la tournée et leur Node associé.
     */
    private final ObservableMap<Pair<Pair<Intersection, Intersection>, Livreur>,
            Line> segments = FXCollections.observableHashMap();

    /**
     * Map contenant les flèches directionnelles des segments de la tournée
     * et leur Node associé.
     */
    private final ObservableMap<Pair<Pair<Intersection, Intersection>, Livreur>,
            Polygon> directions = FXCollections.observableHashMap();

    /**
     * Point de livraison à mettre en avant car sélectionné par l'utilisateur.
     */
    private Pair<Intersection, Circle> pointSelectionne;

    /**
     * Livraison à mettre en avant car sélectionnée par l'utilisateur.
     */
    private Pair<Livraison, FadeTransition> livraisonSelectionnee;

    /**
     * Point particulier représentant l'entrepôt.
     */
    private Pair<Intersection, ImageView> entrepot;

    private boolean afficherPointsDeLivraison = false;

    public CalquePlan() { }

    /**
     * Ajoute un point sur le calque.
     * @param point
     */
    public void ajouterPoint(final Intersection point) {
        Circle cercle = new Circle(TAILLE_POINT, COULEUR_POINT);
        cercle.setVisible(false); // les points sont masqués par défaut
        cercle.setCursor(Cursor.HAND);

        points.put(point, cercle);
        this.getChildren().add(cercle);
        this.markDirty();
    }

    /**
     * Ajoute une livraison sur le calque.
     * @param livraison
     * @param visible
     * @param enRetard
     * @return l'objet Node correspondant à la livraison sur le calque
     */
    public Node ajouterLivraison(final Livraison livraison,
                                 final boolean visible,
                                 final boolean enRetard) {
        // le point de livraison n'est plus accessible
        Node point = points.get(livraison.getAdresse());
        point.setDisable(true);
        point.setVisible(false);

        // création du Node de la livraison
        Shape forme = this.getForme(livraison.getFenetreDeLivraison());
        if (enRetard) {
            forme.setFill(COULEUR_RETARD);
        } else {
            forme.setFill(this.getCouleur(livraison.getLivreur()));
        }

        Text texte = new Text(String.valueOf(livraison.getNumero()));
        texte.setFill(Color.WHITE);
        texte.setStyle("-fx-font-size: " + (15 - 2 * texte.getText().length()));

        StackPane stack = new StackPane();
        stack.setCursor(Cursor.HAND);
        stack.setVisible(visible);
        stack.getChildren().addAll(forme, texte);

        livraisons.put(livraison, stack);
        this.getChildren().add(stack);
        this.markDirty();

        return stack;
    }

    public void modifierLivraisonEnRetard(final Livraison livraison) {
        enleverLivraison(livraison);
        ajouterLivraison(livraison, true, true);
    }

    /**
     * Enlève une livraison du calque.
     * @param livraison
     */
    public void enleverLivraison(final Livraison livraison) {
        // le point de livraison redevient accessible
        Node point = points.get(livraison.getAdresse());
        point.setDisable(false);
        point.setVisible(this.afficherPointsDeLivraison);

        Node noeud = livraisons.get(livraison);
        livraisons.remove(livraison);
        this.getChildren().remove(noeud);
        this.markDirty();
    }

    /**
     * Ajoute un segment sur le calque.
     * @param point1
     * @param point2
     * @param livreur
     */
    public void ajouterSegment(final Intersection point1,
                               final Intersection point2,
                               final Livreur livreur) {
        Line ligne = new Line();
        ligne.setFill(this.getCouleur(livreur));
        ligne.setStroke(this.getCouleur(livreur));
        ligne.setStrokeWidth(TAILLE_SEGMENT);

        Polygon direction = new Polygon();
        direction.setFill(this.getCouleur(livreur));
        direction.setStroke(this.getCouleur(livreur));
        direction.setStrokeWidth(TAILLE_FLECHE);

        segments.put(new Pair(new Pair(point1, point2), livreur), ligne);
        directions.put(new Pair(new Pair(point1, point2), livreur), direction);

        // ajout en position 0 pour que ce soit l'élément le plus en arrière
        this.getChildren().add(0, ligne);
        this.getChildren().add(0, direction);
        this.markDirty();
    }

    /**
     * Enlève un segment et sa direction du calque.
     * @param point1
     * @param point2
     * @param livreur
     */
    public void enleverSegment(final Intersection point1,
                                 final Intersection point2,
                                 final Livreur livreur) {
        Pair<Pair<Intersection, Intersection>, Livreur> segment
                = new Pair(new Pair(point1, point2), livreur);
        Line ligne = segments.get(segment);
        Polygon direction = directions.get(segment);
        segments.remove(segment);
        directions.remove(segment);
        this.getChildren().remove(ligne);
        this.getChildren().remove(direction);
        this.markDirty();
    }

    public void setEntrepot(final Intersection entrepot) {
        ImageView imageView = new ImageView(IMAGE_ENTREPOT);
        this.entrepot = new Pair(entrepot, imageView);
        this.getChildren().add(imageView);
        this.markDirty();
    }

    public void setPointSelectionne(final Intersection intersection) {
        Circle nouvelleSelection = points.get(intersection);

        if (this.pointSelectionne != null) {
            // réinitialisation si une sélection a déjà eu lieu
            Circle ancienneSelection = pointSelectionne.getValue();

            ancienneSelection.setFill(COULEUR_POINT);
            ancienneSelection.setRadius(TAILLE_POINT);
            ancienneSelection.setCenterY(0);
            ancienneSelection.setVisible(this.afficherPointsDeLivraison);
        }

        if (nouvelleSelection != null) {
            nouvelleSelection.setFill(
                    new ImagePattern(IMAGE_POINT_SELECTIONNE));
            nouvelleSelection.setRadius(15);
            // modification du Y pour bien positionner le pin
            nouvelleSelection.setCenterY(nouvelleSelection.getCenterY()
                    - nouvelleSelection.getRadius());
            nouvelleSelection.setVisible(true);

            this.pointSelectionne = new Pair<>(intersection, nouvelleSelection);
        } else {
            this.pointSelectionne = null;
        }
    }

    public void setLivraisonSelectionnee(final Livraison livraison) {
        Node noeud = livraisons.get(livraison);
        FadeTransition animation;

        if (this.livraisonSelectionnee != null) {
            // réinitialisation si une sélection a déjà eu lieu
            animation = livraisonSelectionnee.getValue();
            animation.jumpTo(Duration.ZERO);
            animation.stop();
            animation.setNode(noeud);
        } else {
            animation = new FadeTransition(Duration.seconds(1), noeud);
            animation.setFromValue(10);
            animation.setToValue(0.1);
            animation.setCycleCount(Animation.INDEFINITE);
            animation.setAutoReverse(true);
        }

        animation.play();
        livraisonSelectionnee = new Pair<>(livraison, animation);
    }

    /**
     * Affiche ou masque les points du calque.
     * @param visible
     */
    public void afficherPoints(final boolean visible) {
        this.afficherPointsDeLivraison = visible;

        Circle cerclePointSelectionne = Optional.ofNullable(pointSelectionne)
                .map(Pair::getValue)
                .orElse(null);

        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            Circle cercle = point.getValue();

            // on ne change pas la visibilité du point sélectionné et des
            // points disable (points avec une livraison)
            if (cercle != cerclePointSelectionne && !cercle.isDisable()) {
                cercle.setVisible(visible);
            }
        }
    }

    public void afficherDonneesLivreurs(final List<Livreur> livreurs) {
        for (Map.Entry<Livraison, Node> l : livraisons.entrySet()) {
            Livraison livraison = l.getKey();
            Node livraisonNoeud = l.getValue();

            // visible si le livreur de la livraison est dans la liste
            livraisonNoeud.setVisible(
                    livreurs.contains(livraison.getLivreur()));
        }

        for (Map.Entry<Pair<Pair<Intersection, Intersection>, Livreur>,
                Line> segment : segments.entrySet()) {
            Node ligne = segment.getValue();

            ligne.setVisible(
                    livreurs.contains(segment.getKey().getValue()));
        }

        for (Map.Entry<Pair<Pair<Intersection, Intersection>, Livreur>,
                Polygon> direction : directions.entrySet()) {
            Node fleche = direction.getValue();

            fleche.setVisible(
                    livreurs.contains(direction.getKey().getValue()));
        }
    }

    /**
     * Retourne le point du calque le plus proche d'un point donné.
     * @param x
     * @param y
     * @return le point du calque le plus proche
     */
    public Intersection trouverPointPlusProche(
            final double x, final double y) {
        if (points.isEmpty()) {
            return null;
        }

        // initialisation
        Intersection pointPlusProche = points.keySet().stream().findFirst()
                .get();
        double distanceMin = this.calculerDistanceEuclidienne(x, y,
                points.get(pointPlusProche).getTranslateX(),
                points.get(pointPlusProche).getTranslateY());

        // calcul de la distance pour chaque point
        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            double distance = this.calculerDistanceEuclidienne(x, y,
                    point.getValue().getTranslateX(),
                    point.getValue().getTranslateY());

            if (distance < distanceMin) {
                distanceMin = distance;
                pointPlusProche = point.getKey();
            }
        }

        return pointPlusProche;
    }

    /**
     * Retourne la distance euclidienne entre deux points.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return la distance euclidienne entre deux points
     */
    private double calculerDistanceEuclidienne(final double x1, final double y1,
                                               final double x2,
                                               final double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Détermine la forme à afficher pour la livraison en fonction de la
     * fenêtre de livraison.
     * @param fenetre la fenêtre de livraison de la livraison
     * @return la forme à afficher
     */
    private Shape getForme(final FenetreDeLivraison fenetre) {
        Shape forme;
        switch (fenetre) {
            case H8_H9:
                forme = new Circle(8);
                break;
            case H9_H10:
                Polygon triangle = new Polygon(0, 0, 9, -18, -9, -18);
                triangle.setRotate(180);

                forme = triangle;
                break;
            case H10_H11:
                forme = new Rectangle(16, 16);
                break;
            case H11_H12:
                Polygon pentagone = new Polygon();
                for (int i = 0; i < 5; i++) {
                    pentagone.getPoints().add(10 * Math.sin(
                            2 * i * Math.PI / 5));
                    pentagone.getPoints().add(10 * Math.cos(
                            2 * i * Math.PI / 5));
                }
                pentagone.setRotate(180 / 5);

                forme = pentagone;
                break;
            default:
                forme = new Circle(8);
        }
        return forme;
    }

    private Color getCouleur(final Livreur livreur) {
        Color couleur;
        switch (livreur) {
            case LIVREUR_1:
                couleur = Color.BLUE;
                break;
            case LIVREUR_2:
                couleur = Color.GREEN;
                break;
            case LIVREUR_3:
                couleur = Color.PURPLE;
                break;
            case LIVREUR_4:
                couleur = Color.ORANGE;
                break;
            default:
                couleur = Color.BLUE;
        }
        return couleur;
    }

    /**
     * Ajuste le positionnement des éléments lorsque la carte est déplacée.
     */
    @Override
    protected void layoutLayer() {
        // Obtenir une référence pour le niveau de zoom
        Point2D mapPointEntrepot = getMapPoint(
                entrepot.getKey().getLatitude(),
                entrepot.getKey().getLongitude());
        // Coordonnées du point de référence 25321689
        Point2D mapPointReference = getMapPoint(45.751118, 4.876261);
        Double zoom = calculerDistanceEuclidienne(mapPointEntrepot.getX(),
                mapPointEntrepot.getY(), mapPointReference.getX(),
                mapPointReference.getY());
        if (zoomReference == -1.0) {
            zoomReference = zoom;
        }

        positionner(entrepot.getKey(), entrepot.getValue());

        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            positionner(point.getKey(), point.getValue());
        }

        for (Map.Entry<Livraison, Node> livraison : livraisons.entrySet()) {
            positionner(livraison.getKey().getAdresse(), livraison.getValue());
        }

        for (Map.Entry<Pair<Pair<Intersection, Intersection>, Livreur>,
                Line> segment : segments.entrySet()) {
            positionner(segment.getKey().getKey(), segment.getValue());
        }

        for (Map.Entry<Pair<Pair<Intersection, Intersection>, Livreur>,
                Polygon> direction : directions.entrySet()) {
            positionner(direction.getKey().getKey(), direction.getValue(),
                    zoom / zoomReference);
        }
    }

    private void positionner(final Intersection intersection,
                             final Node noeud) {
        Point2D mapPoint = getMapPoint(intersection.getLatitude(),
                intersection.getLongitude());
        noeud.setTranslateX(mapPoint.getX());
        noeud.setTranslateY(mapPoint.getY());
    }

    private void positionner(final Pair<Intersection, Intersection> segment,
                             final Line ligne) {
        Intersection point1 = segment.getKey();
        Intersection point2 = segment.getValue();

        Point2D mapPoint1 = getMapPoint(
                point1.getLatitude(), point1.getLongitude());
        Point2D mapPoint2 = getMapPoint(
                point2.getLatitude(), point2.getLongitude());

        ligne.setStartX(mapPoint1.getX());
        ligne.setStartY(mapPoint1.getY());
        ligne.setEndX(mapPoint2.getX());
        ligne.setEndY(mapPoint2.getY());
    }

    private void positionner(final Pair<Intersection, Intersection> segment,
                             final Polygon direction, final Double zoom) {
        Intersection point1 = segment.getKey();
        Intersection point2 = segment.getValue();

        Point2D mapPoint1 = getMapPoint(
                point1.getLatitude(), point1.getLongitude());
        Point2D mapPoint2 = getMapPoint(
                point2.getLatitude(), point2.getLongitude());
        // Localisation du centre de la flèche sur le segment
        Double pourcentageCentreFleche = 0.75;
        Point2D mapPointCentreFleche = new Point2D(
                mapPoint1.getX() + (mapPoint2.getX() - mapPoint1.getX())
                        * pourcentageCentreFleche,
                mapPoint1.getY() + (mapPoint2.getY() - mapPoint1.getY())
                        * pourcentageCentreFleche);
        // On normalise les flèches pour qu'elles aient la même taille
        Double norme = calculerDistanceEuclidienne(mapPoint1.getX(),
                mapPoint1.getY(), mapPoint2.getX(), mapPoint2.getY());
        Double diffX = (mapPoint2.getX() - mapPoint1.getX()) / norme;
        Double diffY = (mapPoint2.getY() - mapPoint1.getY()) / norme;
        Double coefficient = zoom;
        if (zoom > 1.0) {
            coefficient = Math.log(zoom);
        } else if (zoom < 1.0) {
            coefficient = 0.0;
        }

        direction.getPoints().setAll(
                mapPointCentreFleche.getX() + coefficient * diffX,
                mapPointCentreFleche.getY() + coefficient * diffY,
                mapPointCentreFleche.getX() + coefficient * (-diffX + diffY),
                mapPointCentreFleche.getY() + coefficient * (-diffY - diffX),
                mapPointCentreFleche.getX() + coefficient * (-diffX - diffY),
                mapPointCentreFleche.getY() + coefficient * (-diffY + diffX));
    }

    public Livraison getLivraisonSelectionnee() {
        if (this.livraisonSelectionnee != null) {
            return this.livraisonSelectionnee.getKey();
        }
        return null;
    }
}
