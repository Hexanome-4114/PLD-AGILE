package com.github.hexanome4114.pldagile.controleur;

import com.github.hexanome4114.pldagile.modele.XMLParser;
import javafx.fxml.FXML;
import org.dom4j.DocumentException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class Controller {

    @FXML
    protected void loadXML() {
        XMLParser parser = new XMLParser();
        URL xmlFile = null;
        try {
            xmlFile = Path.of("").toUri().toURL();
            System.out.println(parser.parse(xmlFile));
        } catch (MalformedURLException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}