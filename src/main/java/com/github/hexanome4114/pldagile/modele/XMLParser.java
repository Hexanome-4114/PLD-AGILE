package com.github.hexanome4114.pldagile.modele;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.swing.*;

public class XMLParser {

    public String getXMLFileUrl() {
        String xmlFileUrl = "";
        FileDialog fileDialog = new FileDialog(new JFrame());
        fileDialog.setVisible(true);
        File[] file = fileDialog.getFiles();
        if(file.length > 0){
            xmlFileUrl = fileDialog.getFiles()[0].getAbsolutePath();
        }
        return xmlFileUrl;
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
