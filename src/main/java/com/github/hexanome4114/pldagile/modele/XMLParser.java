package com.github.hexanome4114.pldagile.modele;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XMLParser {

    public String getXMLFilePath(Stage stage) {
        String xmlFilePath = "";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selectionnez un fichier xml");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
                );

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            xmlFilePath = file.getPath();
        }
        return xmlFilePath;
    }

    public Document parse(URL url) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public Long getWarehouse(Document document) {
        Node node = document.selectSingleNode("//warehouse");

        //System.out.println("Current element : " + node.getName()
          //      + " | address : " + node.valueOf("@address"));
        return Long.parseLong(node.valueOf("@address"));
    }

    public HashMap<Long, Double[]> getIntersection(Document document) {
        List<Node> nodes = document.selectNodes("//intersection");

        HashMap<Long, Double[]> hashMap = new HashMap<>();
        for (Node node: nodes) {
//            System.out.println("Current element : " + node.getName()
//                    + " | longitude : " + node.valueOf("@longitude")
//                    + ", latitude : " + node.valueOf("@latitude")
//                    + ", id : " + node.valueOf("@id"));
            Long id = Long.parseLong(node.valueOf("@id"));
            Double coords[] = new Double[2];
            coords[0] = Double.parseDouble(node.valueOf("@longitude"));
            coords[1] = Double.parseDouble(node.valueOf("@latitude"));
            hashMap.put(id,coords);
        }
        return hashMap;
    }

    public List<Long[]> getSegment(Document document) {
        List<Node> nodes = document.selectNodes("//segment");
        List<Long[]> listSegments = new ArrayList<>();

        for (Node node: nodes) {
            // TODO Les rues sont souvent à doubles sens. Or là j'enregistre seulement une des rues (id de la map sur le nom)
            String name = node.valueOf("@name");
            Long idDestOrigin[] = new Long[2];
            idDestOrigin[0] = Long.parseLong(node.valueOf("@destination"));
            idDestOrigin[1] = Long.parseLong(node.valueOf("@origin"));
            listSegments.add(idDestOrigin);
//            System.out.println("Current element : " + node.getName()
//                    + " | origin : " + node.valueOf("@origin")
//                    + ", name : " + node.valueOf("@name")
//                    + ", length : " + node.valueOf("@length")
//                    + ", destination : " + node.valueOf("@destination"));

        }
        return listSegments;
    }
}
