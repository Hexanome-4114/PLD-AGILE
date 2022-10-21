package com.github.hexanome4114.pldagile.controleur;

import javafx.fxml.FXML;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import com.github.hexanome4114.pldagile.modele.XMLParser;

public class Controller {

    @FXML
    protected void loadXML() {
        XMLParser parser = new XMLParser();
        String xmlFileUrl = "";
        URL xmlFileURL = null;
        Document xmlFile = null;
        try {
            xmlFileUrl = parser.getXMLFileUrl();
            xmlFileURL = Path.of(xmlFileUrl).toUri().toURL();
            xmlFile = parser.parse(xmlFileURL);
            parser.getWarehouse(xmlFile);
            parser.getIntersection(xmlFile);
            parser.getSegment(xmlFile);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}