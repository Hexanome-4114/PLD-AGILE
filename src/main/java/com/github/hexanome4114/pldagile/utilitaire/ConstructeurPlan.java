package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;

import java.util.HashMap;
import java.util.Map;

public class ConstructeurPlan {

    /**
     * Constructeur privé pour avoir un comportement statique.
     */
    private ConstructeurPlan() { }

    public static HashMap<Long, Double[]> coordonneesVersPixels(final HashMap<Long, Intersection> intersections, final int largeur, final int hauteur) {
        Double minX = -1., minY = -1.;
        Double maxX = -1., maxY = -1.;
        HashMap<Long, Double[]> mapCoords = new HashMap<>(intersections.size());
        HashMap<Long, Double[]> mapCoordsPixel = new HashMap<>(intersections.size()); // result

        for (Map.Entry<Long, Intersection> entry : intersections.entrySet()) {
            // Convertir en radian
            double longitude = entry.getValue().getLongitude() * Math.PI / 180;
            double latitude = entry.getValue().getLatitude() * Math.PI / 180;

            // Projection de Mercator
            Double[] coordsXY = new Double[2];
            coordsXY[0] = longitude;
            coordsXY[1] = Math.log(Math.tan(Math.PI / 4.0 + latitude / 2.));

            // The reason we need to determine the min X and Y values is because in order to
            // draw the map,
            // we need to offset the position so that there will be no negative X and Y
            // values
            minX = (minX == -1.) ? coordsXY[0] : Math.min(minX, coordsXY[0]);
            minY = (minY == -1.) ? coordsXY[1] : Math.min(minY, coordsXY[1]);

            mapCoords.put(entry.getKey(), coordsXY);
        }

        // readjust coordinate to ensure there are no negative values
        for (var entryIterator = mapCoords.entrySet().iterator(); entryIterator.hasNext(); ) {
            var entry = entryIterator.next();
            Double[] coordsXY = new Double[2];
            //coordsXY[0] = (minX < 0.)? entry.getValue()[0] - minX : entry.getValue()[0];
            //coordsXY[1] = (minY < 0.)? entry.getValue()[1] - minY : entry.getValue()[1];
            coordsXY[0] = entry.getValue()[0] - minX;
            coordsXY[1] = entry.getValue()[1] - minY;
            entry.setValue(coordsXY);

            // now, we need to keep track the max X and Y values
            maxX = (maxX == -1) ? coordsXY[0] : Math.max(maxX, coordsXY[0]);
            maxY = (maxY == -1) ? coordsXY[1] : Math.max(maxY, coordsXY[1]);
        }

        // determine the width and height ratio because we need to magnify the map to fit into the given image dimension
        double mapWidthRatio = largeur / maxX;
        double mapHeightRatio = hauteur / maxY;
        // using different ratios for width and height will cause the map to be stretched. So, we have to determine
        // the global ratio that will perfectly fit into the given image dimension
        double globalRatio = Math.min(mapWidthRatio, mapHeightRatio);

        // now we need to readjust the padding to ensure the map is always drawn in the center of the given image dimension
        double heightPadding = (hauteur - (globalRatio * maxY)) / 2;
        double widthPadding = (largeur - (globalRatio * maxX)) / 2;
        for (var entryIterator = mapCoords.entrySet().iterator(); entryIterator.hasNext(); ) {
            var entry = entryIterator.next();
            Double[] coordsXYPixels = new Double[2];
            coordsXYPixels[0] = (widthPadding + (entry.getValue()[0] * globalRatio));

            // need to invert the Y since 0,0 starts at top left
            coordsXYPixels[1] = (hauteur - heightPadding - (entry.getValue()[1] * globalRatio));
            mapCoordsPixel.put(entry.getKey(), coordsXYPixels);
        }

        return mapCoordsPixel;
    }
}
