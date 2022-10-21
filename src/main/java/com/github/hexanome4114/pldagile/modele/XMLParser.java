package com.github.hexanome4114.pldagile.modele;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    public void getWarehouse(Document document) {
        Node node = document.selectSingleNode("//warehouse");

        System.out.println("Current element : " + node.getName()
                + " | address : " + node.valueOf("@address"));
    }

    public void getIntersection(Document document) {
        List<Node> nodes = document.selectNodes("//intersection");

        for (Node node: nodes) {
            System.out.println("Current element : " + node.getName()
                    + " | longitude : " + node.valueOf("@longitude")
                    + ", latitude : " + node.valueOf("@latitude")
                    + ", id : " + node.valueOf("@id"));
        }
    }

    public void getSegment(Document document) {
        List<Node> nodes = document.selectNodes("//segment");

        for (Node node: nodes) {
            System.out.println("Current element : " + node.getName()
                    + " | origin : " + node.valueOf("@origin")
                    + ", name : " + node.valueOf("@name")
                    + ", length : " + node.valueOf("@length")
                    + ", destination : " + node.valueOf("@destination"));
        }
    }
}
