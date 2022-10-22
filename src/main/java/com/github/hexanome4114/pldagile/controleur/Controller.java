package com.github.hexanome4114.pldagile.controleur;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import com.github.hexanome4114.pldagile.modele.XMLParser;

import javax.crypto.Cipher;

public class Controller {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void loadXML() {
        XMLParser parser = new XMLParser();
        String xmlFilePath;
        URL xmlFileURL = null;
        Document xmlFile = null;
        Long entrepot = null;
        HashMap<Long, Double[]> mapLngLat = null;
        try {
            xmlFilePath = parser.getXMLFilePath(this.stage);
            if (!xmlFilePath.equals("")) {
                xmlFileURL = Path.of(xmlFilePath).toUri().toURL();
                xmlFile = parser.parse(xmlFileURL);
                entrepot = parser.getWarehouse(xmlFile);
                mapLngLat = parser.getIntersection(xmlFile);
                parser.getSegment(xmlFile);
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }

        int width = 640;
        int height = 640;

        HashMap<Long, Double[]> mapCoordsPixel= dessineMap(entrepot, mapLngLat, width, height);
        Group group = new Group();
        mapCoordsPixel.forEach((aLong, doubles) -> {
            Line line = new Line(doubles[0], doubles[1], doubles[0], doubles[1]);
            group.getChildren().add(line);
        });


        List<Long[]> listSegments = parser.getSegment(xmlFile);
        listSegments.forEach((longs) -> {
            Double coordsDestination[] = mapCoordsPixel.get(longs[0]);
            Double coordsOrigin[] = mapCoordsPixel.get(longs[1]);
            Line line = new Line(coordsOrigin[0], coordsOrigin[1], coordsDestination[0], coordsDestination[1]);
            group.getChildren().add(line);
        });

        Scene scene = new Scene(group, width, height);
        Stage stage1 = new Stage();
        stage1.setScene(scene);
        this.setStage(stage1);
        stage1.show();
    }

    // Projection de Mercator https://fr.wikipedia.org/wiki/Projection_de_Mercator
    public HashMap<Long, Double[]> dessineMap(Long entrepot, HashMap<Long, Double[]> mapLngLat, int width, int height){
        Double minX=-1., minY=-1.;
        Double maxX=-1., maxY=-1.;
        HashMap<Long, Double[]> mapCoords = new HashMap<>(mapLngLat.size());
        HashMap<Long, Double[]> mapCoordsPixel = new HashMap<>(mapLngLat.size()); // result


        // https://stackoverflow.com/questions/5983099/converting-longitude-latitude-to-x-y-coordinate
        for (var entryIterator = mapLngLat.entrySet().iterator(); entryIterator.hasNext(); ) {
            var entry = entryIterator.next();
            // Convertir en radian
            double longitude = entry.getValue()[0] * Math.PI / 180;
            double latitude = entry.getValue()[1] * Math.PI / 180;

            // Projection de Mercator
            Double coordsXY[] = new Double[2];
            coordsXY[0] = longitude;
            coordsXY[1] = Math.log(Math.tan(Math.PI / 4.0 + latitude / 2.));

            // The reason we need to determine the min X and Y values is because in order to draw the map,
            // we need to offset the position so that there will be no negative X and Y values
            minX = (minX == -1.) ? coordsXY[0] : Math.min(minX, coordsXY[0] );
            minY = (minY == -1.) ? coordsXY[1] : Math.min(minY, coordsXY[1]);

            mapCoords.put(entry.getKey(), coordsXY);
        };
        System.out.println("minX: "+minX +" minY: "+minY);
        mapCoords.forEach((aLong, doubles) -> {
            System.out.println(aLong+", "+doubles[0]+", "+ doubles[1]);
        });

        // readjust coordinate to ensure there are no negative values
        for (var entryIterator = mapCoords.entrySet().iterator(); entryIterator.hasNext(); ) {
            var entry = entryIterator.next();
            Double coordsXY[] = new Double[2];
            //coordsXY[0] = (minX < 0.)? entry.getValue()[0] - minX : entry.getValue()[0];
            //coordsXY[1] = (minY < 0.)? entry.getValue()[1] - minY : entry.getValue()[1];
            coordsXY[0] = entry.getValue()[0] - minX;
            coordsXY[1] = entry.getValue()[1] - minY;
            entry.setValue(coordsXY);

            // now, we need to keep track the max X and Y values
            maxX = (maxX == -1) ? coordsXY[0] : Math.max(maxX, coordsXY[0]);
            maxY = (maxY == -1) ? coordsXY[1] : Math.max(maxY, coordsXY[1]);
        }
        System.out.println("maxX: "+maxX +" maxY: "+maxY);

        // determine the width and height ratio because we need to magnify the map to fit into the given image dimension
        double mapWidthRatio = width / maxX;
        double mapHeightRatio = height / maxY;
        // using different ratios for width and height will cause the map to be stretched. So, we have to determine
        // the global ratio that will perfectly fit into the given image dimension
        double globalRatio = Math.min(mapWidthRatio, mapHeightRatio);

        // now we need to readjust the padding to ensure the map is always drawn in the center of the given image dimension
        double heightPadding = (height - (globalRatio * maxY)) / 2;
        double widthPadding = (width - (globalRatio * maxX)) / 2;
        System.out.println("TRADUCTION en PIXEL \n\n");
        for (var entryIterator = mapCoords.entrySet().iterator(); entryIterator.hasNext(); ) {
            var entry = entryIterator.next();
            Double coordsXYPixels[] = new Double[2];
            coordsXYPixels[0] = (widthPadding + (entry.getValue()[0] * globalRatio));

            // need to invert the Y since 0,0 starts at top left
            coordsXYPixels[1] = (height - heightPadding - (entry.getValue()[1] * globalRatio));
            mapCoordsPixel.put(entry.getKey(), coordsXYPixels);
            System.out.println(entry.getKey()+", "+coordsXYPixels[0]+", "+ coordsXYPixels[1]);
        }


        return mapCoordsPixel;
    }
}
