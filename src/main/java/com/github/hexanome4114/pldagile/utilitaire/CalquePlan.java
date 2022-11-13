package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;
import com.gluonhq.maps.MapLayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Pair;
//TODO ajout de java.util.List;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private final ObservableList<Pair<Intersection, Node>> points =
            FXCollections.observableArrayList();

    // TODO EN COURS DE MODIF
    private final ObservableList<Pair<List<Intersection>, Node>> segments =
            FXCollections.observableArrayList();

    public CalquePlan() { }

    public void ajouterPoint(final Intersection p, final Node icon) {
        points.add(new Pair(p, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    // TODO EN COURS DE MODIF
    public void ajouterSegment(final Intersection p1, final Intersection p2, final Node icon){
        List intersections = new ArrayList();
        intersections.add(p1);
        intersections.add(p2);
        segments.add(new Pair(intersections, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (Pair<Intersection, Node> candidate : points) {
            Intersection point = candidate.getKey();
            Node icon = candidate.getValue();
            Point2D mapPoint = getMapPoint(
                    point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }
        //TODO EN COURS DE MODIF
        for (Pair<List<Intersection>, Node> candidate : segments) {
            Node icon = candidate.getValue();
            icon.setVisible(true);
        }
    }

    public ObservableList<Pair<Intersection, Node>> getPoints() {
        return points;
    }

    //TODO EN COURS DE MODIF
    public ObservableList<Pair<List<Intersection>, Node>> getSegments() { return segments; }
}
