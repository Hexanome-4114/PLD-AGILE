package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.controleur.Controleur;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.modele.Segment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Sérialisation et désérialisation des données.
 */
public final class Serialiseur {

    /**
     * Constructeur privé pour avoir un comportement statique.
     */
    private Serialiseur() { }

    /**
     * Charge un plan depuis un fichier XML.
     *
     * @param fichier le fichier source
     * @return le plan
     */
    public static Plan chargerPlan(final File fichier)
            throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(fichier);

        // intersections
        HashMap<Long, Intersection> intersections = new HashMap<>();

        for (Node noeudIntersection : document.selectNodes("//intersection")) {
            Long id = Long.parseLong(noeudIntersection.valueOf("@id"));
            double latitude = Double.parseDouble(
                    noeudIntersection.valueOf("@latitude"));
            double longitude = Double.parseDouble(
                    noeudIntersection.valueOf("@longitude"));

            intersections.put(id, new Intersection(latitude, longitude));
        }

        // entrepot
        Node node = document.selectSingleNode("//warehouse");
        Intersection entrepot = intersections.get(
                Long.parseLong(node.valueOf("@address")));

        // segments
        List<Segment> segments = new ArrayList<>();

        for (Node noeudSegment : document.selectNodes("//segment")) {
            // TODO Les rues sont souvent à doubles sens. Or là j'enregistre
            // seulement une des rues (id de la map sur le nom)

            String nom = noeudSegment.valueOf("@name");
            int longueur = (int) Math.round(Double.parseDouble(
                    noeudSegment.valueOf("@length")) * 100); // stockage en cm

            Intersection intersectionDebut = intersections.get(
                    Long.parseLong(noeudSegment.valueOf("@origin")));
            Intersection intersectionFin = intersections.get(
                    Long.parseLong(noeudSegment.valueOf("@destination")));

            segments.add(new Segment(
                    nom, longueur, intersectionDebut, intersectionFin));
        }

        return new Plan(segments, entrepot);
    }

    /**
     * Sauvegarde un ensemble de livraisons dans un fichier XML.
     *
     * @param fichier    le fichier de destination
     * @param livraisons les livraisons à sauvegarder
     */
    public static void sauvegarderLivraisons(final File fichier,
            final List<Livraison> livraisons) throws IOException {

        Document document = DocumentHelper.createDocument();
        Element racine = document.addElement("livraisons");

        for (Livraison livraison : livraisons) {
            Element livraisonElement = racine.addElement("livraison");

            livraisonElement.addElement("numero")
                    .addText(String.valueOf(livraison.getNumero()));

            // livreur
            Livreur livreur = livraison.getLivreur();
            Element livreurElement = livraisonElement.addElement("livreur");
            livreurElement.addElement("numero")
                    .addText(String.valueOf(livreur.getNumero()));

            // fenêtre de livraison
            FenetreDeLivraison fenetre = livraison.getFenetreDeLivraison();
            Element fenetreElement = livraisonElement.addElement("fenetre");
            fenetreElement.addElement("debut")
                    .addText(String.valueOf(fenetre.getDebut()));
            fenetreElement.addElement("fin")
                    .addText(String.valueOf(fenetre.getFin()));

            // adresse
            Intersection adresse = livraison.getAdresse();
            Element adresseElement = livraisonElement.addElement("adresse");
            adresseElement.addElement("latitude")
                    .addText(String.valueOf(adresse.getLatitude()));
            adresseElement.addElement("longitude")
                    .addText(String.valueOf(adresse.getLongitude()));
        }

        XMLWriter writer = new XMLWriter(new FileOutputStream(fichier),
                OutputFormat.createPrettyPrint());
        writer.write(document);
        writer.close();
    }

    /**
     * Charge un ensemble de livraisons depuis un fichier XML.
     *
     * @param fichier le fichier source
     * @return un ensemble de livraisons
     */
    public static List<Livraison> chargerLivraisons(final File fichier)
            throws DocumentException {
        List<Livraison> livraisons = new ArrayList<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(fichier);

        for (Node noeudLivraison : document.selectNodes("//livraison")) {
            Node noeudNumero = noeudLivraison.selectSingleNode("numero");
            int numero = Integer.parseInt(noeudNumero.getText());

            Node noeudFenetre = noeudLivraison.selectSingleNode("fenetre");
            FenetreDeLivraison fenetre = new FenetreDeLivraison(
                    Integer.parseInt(
                            noeudFenetre.selectSingleNode("debut").getText()),
                    Integer.parseInt(
                            noeudFenetre.selectSingleNode("fin").getText())
            );

            Node noeudLivreur = noeudLivraison.selectSingleNode("livreur");
            Livreur livreur = new Livreur(
                    Integer.parseInt(
                            noeudLivreur.selectSingleNode("numero").getText()),
                    Controleur.VITESSE_MOYENNE
            );

            Node noeudAdresse = noeudLivraison.selectSingleNode("adresse");
            double latitude = Double.parseDouble(
                    noeudAdresse.selectSingleNode("latitude").getText());
            double longitude = Double.parseDouble(
                    noeudAdresse.selectSingleNode("longitude").getText());
            Intersection adresse = new Intersection(latitude, longitude);

            livraisons.add(new Livraison(numero, fenetre, livreur, adresse));
        }

        return livraisons;
    }
}
