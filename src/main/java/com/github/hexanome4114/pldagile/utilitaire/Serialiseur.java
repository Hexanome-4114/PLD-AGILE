package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Plan;
import javafx.util.Pair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.net.URL;
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
     * @param url l'URL du fichier source
     * @return le plan
     */
    public static Plan chargerPlan(final URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);

        // intersections
        HashMap<String, Intersection> intersections = new HashMap<>();

        for (Node noeudIntersection: document.selectNodes("/map/intersection")) {
            String id = noeudIntersection.valueOf("@id");
            double latitude = Double.parseDouble(
                    noeudIntersection.valueOf("@latitude"));
            double longitude = Double.parseDouble(
                    noeudIntersection.valueOf("@longitude"));

            intersections.put(id, new Intersection(id, latitude, longitude));
        }

        // entrepot
        Node node = document.selectSingleNode("/map/warehouse");
        Intersection entrepot = intersections.get(
                node.valueOf("@address"));


        for (Node noeudSegment: document.selectNodes("/map/segment")) {
            String nom = noeudSegment.valueOf("@name");
            int longueur = (int) Math.round(Double.parseDouble(
                    noeudSegment.valueOf("@length")) * 100); // stockage en cm

            Intersection intersectionDebut = intersections.get(
                    noeudSegment.valueOf("@origin"));
            Intersection intersectionFin = intersections.get(
                    noeudSegment.valueOf("@destination"));

        // Ajout dans l'intersection début du lien avec l'intersection de fin (modélisation du segment)
            intersectionDebut.getIntersections()
                    .put(intersectionFin, new Pair<Integer, String>(longueur, nom));
        }

        return new Plan(intersections, entrepot);
    }

    /**
     * Sauvegarde un ensemble de livraisons dans un fichier XML.
     *
     * @param url        l'URL du fichier de destination
     * @param livraisons les livraisons à sauvegarder
     */
    public static void sauvegarderLivraisons(final URL url,
            final List<Livraison> livraisons) {

    }

    /**
     * Charge un ensemble de livraisons depuis un fichier XML.
     *
     * @param url l'URL du fichier source
     * @return un ensemble de livraisons
     */
    public static List<Livraison> chargerLivraisons(final URL url) {
        return null;
    }
}
