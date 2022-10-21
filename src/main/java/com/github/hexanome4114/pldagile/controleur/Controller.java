package com.github.hexanome4114.pldagile.controleur;

import javafx.fxml.FXML;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import com.github.hexanome4114.pldagile.modele.XMLParser;

public class Controller {

    @FXML
    protected void loadXML() {
        XMLParser parser = new XMLParser();
        URL xmlFile = null;
        String xmlFileUrl = "";
        try {
            xmlFileUrl = parser.getXmlFileUrl();
            xmlFile = Path.of(xmlFileUrl).toUri().toURL();
            System.out.println(parser.parse(xmlFile));
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}