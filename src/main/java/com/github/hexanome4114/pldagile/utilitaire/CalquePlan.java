package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;
import com.gluonhq.maps.MapLayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Pair;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private final ObservableList<Pair<Intersection, Node>> points =
            FXCollections.observableArrayList();

    public CalquePlan() { }

    public void ajouterPoint(final Intersection p, final Node icon) {
        final Pair<Intersection, Node> pair = new Pair(p, icon);
        icon.setOnMouseClicked(e -> {

            e.consume();
            System.out.println(pair.getKey());
        });
        points.add(pair);
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
    }
}
