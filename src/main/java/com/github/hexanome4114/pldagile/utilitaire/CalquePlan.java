package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;
import com.gluonhq.maps.MapLayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private static final int TAILLE_POINT = 4;

    private static final Color COULEUR_POINT = Color.GREY;

    private static final Image IMAGE_ENTREPOT = new Image(
            CalquePlan.class.getResource("/images/entrepot.png").toString(),
            25, 25, false, false);

    private static final Image IMAGE_POINT_SELECTIONNE = new Image(
            CalquePlan.class.getResource("/images/pin.png").toString());

    /**
     * Liste contenant les points de livraison et leur objet Icon associé.
     */
    private final ObservableList<Pair<Intersection, Circle>> points =
            FXCollections.observableArrayList();

    /**
     * Point de livraison sélectionné par l'utilisateur.
     */
    private Circle pointSelectionne;

    /**
     * Point particulier représentant l'entrepôt.
     */
    private Pair<Intersection, ImageView> entrepot;

    public CalquePlan() { }

    /**
     * Ajoute un point sur le calque.
     * @param point
     */
    public void ajouterPoint(final Intersection point) {
        Circle cercle = new Circle(TAILLE_POINT, COULEUR_POINT);
        cercle.setVisible(false); // les points sont masqués par défaut
        cercle.setCursor(Cursor.HAND);

        points.add(new Pair(point, cercle));
        this.getChildren().add(cercle);
        this.markDirty();
    }

    public void setEntrepot(final Intersection entrepot) {
        ImageView imageView = new ImageView(IMAGE_ENTREPOT);
        this.entrepot = new Pair(entrepot, imageView);
        this.getChildren().add(imageView);
        this.markDirty();
    }

    public void setPointSelectionne(final Circle point) {
        if (this.pointSelectionne != null) {
            // réinitialisation si une sélection a déjà eu lieu
            this.pointSelectionne.setFill(COULEUR_POINT);
            this.pointSelectionne.setRadius(TAILLE_POINT);
            this.pointSelectionne.setCenterY(0);
            this.pointSelectionne.setVisible(point.isVisible());
        }

        point.setFill(new ImagePattern(IMAGE_POINT_SELECTIONNE));
        point.setRadius(15);
        // modification du Y pour bien positionner le pin
        point.setCenterY(point.getCenterY() - point.getRadius());
        point.setVisible(true);

        this.pointSelectionne = point;
    }

    /**
     * Affiche ou masque les points du calque.
     * @param visible
     */
    public void afficherPoints(final boolean visible) {
        for (Pair<Intersection, Circle> candidate : points) {
            Circle cercle = candidate.getValue();

            // on ne masque jamais le point sélectionné
            if (cercle != this.pointSelectionne) {
                cercle.setVisible(visible);
            }
        }
    }

    /**
     * Retourne le point du calque le plus proche d'un point donné.
     * @param x
     * @param y
     * @return le point du calque le plus proche
     */
    public Pair<Intersection, Circle> trouverPointPlusProche(
            final double x, final double y) {
        if (points.isEmpty()) {
            return null;
        }

        // initialisation
        Pair<Intersection, Circle> pointPlusProche = points.get(0);
        double distanceMin = this.calculerDistanceEuclidienne(x, y,
                pointPlusProche.getValue().getTranslateX(),
                pointPlusProche.getValue().getTranslateY());

        // calcul de la distance pour chaque point
        for (Pair<Intersection, Circle> point : points) {
            double distance = this.calculerDistanceEuclidienne(x, y,
                    point.getValue().getTranslateX(),
                    point.getValue().getTranslateY());

            if (distance < distanceMin) {
                distanceMin = distance;
                pointPlusProche = point;
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
            final double x2, final double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return diffX * diffX + diffY * diffY;
    }

    @Override
    protected void layoutLayer() {
        for (Pair<Intersection, Circle> candidate : points) {
            Intersection point = candidate.getKey();
            Circle icon = candidate.getValue();

            Point2D mapPoint = getMapPoint(
                    point.getLatitude(), point.getLongitude());
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }

        Intersection point = entrepot.getKey();
        ImageView icon = entrepot.getValue();

        Point2D mapPoint = getMapPoint(point.getLatitude(),
                point.getLongitude());
        icon.setTranslateX(mapPoint.getX());
        icon.setTranslateY(mapPoint.getY());
    }

    public ObservableList<Pair<Intersection, Circle>> getPoints() {
        return points;
    }
}
