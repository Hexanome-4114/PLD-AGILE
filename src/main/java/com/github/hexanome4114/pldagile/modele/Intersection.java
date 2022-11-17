package com.github.hexanome4114.pldagile.modele;

import com.gluonhq.maps.MapPoint;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Objects;

public final class Intersection extends MapPoint {

    private final String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intersection that = (Intersection) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * @intersections correspond à la liste des intersections rejoignables
     * depuis l'intersection courante, avec la longueur et le nom de la rue
     */
    private final HashMap<Intersection, Pair<Integer, String>> intersections;

    public Intersection(final String id, final double latitude,
                        final double longitude) {
        super(latitude, longitude);
        this.id = id;
        intersections = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public HashMap<Intersection, Pair<Integer, String>> getIntersections() {
        return intersections;
    }

    @Override
    public String toString() {
        return "Intersection{" + getId() + '}';
    }
}
