package com.github.hexanome4114.pldagile.modele;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import javax.swing.*;

public class XMLParser {

    public String getXmlFileUrl() {
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
}
