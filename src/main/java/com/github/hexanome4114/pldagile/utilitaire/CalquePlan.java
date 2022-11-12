package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;
import com.gluonhq.maps.MapLayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private final ObservableList<Pair<Intersection, Circle>> points =
            FXCollections.observableArrayList();

    public CalquePlan() { }

    /**
     * Ajoute un point sur le calque.
     * @param p
     * @param icon
     */
    public void ajouterPoint(final Intersection p, final Circle icon) {
        points.add(new Pair(p, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    /**
     * Affiche ou masque les points du calque.
     * @param visible
     */
    public void afficherPoints(final boolean visible) {
        for (Pair<Intersection, Circle> candidate : points) {
            Circle cercle = candidate.getValue();
            cercle.setVisible(visible);
        }
    }

    /**
     * Retourne le point du calque le plus proche d'un point donné.
     * @param x
     * @param y
     * @return le point du calque le plus proche
     */
    public Pair<Intersection, Circle> trouverPointPlusProche(double x, double y) {
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
    private double calculerDistanceEuclidienne(double x1, double y1, double x2, double y2) {
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
    }

    public ObservableList<Pair<Intersection, Circle>> getPoints() {
        return points;
    }
}
