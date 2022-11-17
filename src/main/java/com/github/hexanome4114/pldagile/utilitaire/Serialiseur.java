package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.*;
import javafx.util.Pair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        HashMap<String, Intersection> intersections = new HashMap<>();

        for (Node noeudIntersection: document
                .selectNodes("/map/intersection")) {
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

            // Ajout dans l'intersection début du lien avec l'intersection
            // de fin (modélisation du segment)
            intersectionDebut.getIntersections().put(intersectionFin,
                    new Pair<Integer, String>(longueur, nom));
        }

        return new Plan(intersections, entrepot);
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
            livreurElement.addAttribute("id", livreur.name());

            // fenêtre de livraison
            FenetreDeLivraison fenetre = livraison.getFenetreDeLivraison();
            Element fenetreElement = livraisonElement.addElement("fenetre");
            fenetreElement.addAttribute("id", fenetre.name());

            // adresse
            Intersection adresse = livraison.getAdresse();
            Element adresseElement = livraisonElement.addElement("adresse");
            adresseElement.addAttribute("id", adresse.getId());
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
     * @param plan
     * @return un ensemble de livraisons
     */
    public static List<Livraison> chargerLivraisons(final File fichier,
                                                    final Plan plan)
            throws DocumentException {
        List<Livraison> livraisons = new ArrayList<>();

        // Set contenant les adresses des livraisons pour vérifier les doublons
        Set<String> adresses = new HashSet<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(fichier);

        for (Node noeudLivraison : document.selectNodes("//livraison")) {
            Node noeudNumero = noeudLivraison.selectSingleNode("numero");
            int numero = Integer.parseInt(noeudNumero.getText());

            // fenêtre de livraison
            Node noeudFenetre = noeudLivraison.selectSingleNode("fenetre");
            FenetreDeLivraison fenetre = FenetreDeLivraison.valueOf(
                    noeudFenetre.valueOf("@id"));

            // livreur
            Node noeudLivreur = noeudLivraison.selectSingleNode("livreur");
            Livreur livreur = Livreur.valueOf(
                    noeudLivreur.valueOf("@id"));

            // adresse
            Node noeudAdresse = noeudLivraison.selectSingleNode("adresse");
            String idAdresse = noeudAdresse.valueOf("@id");

            if (!plan.getIntersections().containsKey(idAdresse)) {
                throw new DocumentException("L'adresse de cette livraison"
                        + "n'existe pas sur le plan.");
            }

            if (!adresses.add(idAdresse)) {
                throw new DocumentException(
                        "Une livraison existe déjà à cette adresse.");
            }

            Intersection adresse = plan.getIntersections().get(idAdresse);

            livraisons.add(new Livraison(numero, fenetre, livreur, adresse));
        }

        return livraisons;
    }

    public static void genererFeuilleDeRoute(final File file,
                                             final Tournee tournee) {
        if (!tournee.isTourneeCalculee()) {
            return;
        }

        PrintWriter ecrivain;
        try {
            ecrivain = new PrintWriter(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // HEADER
        ecrivain.println("Tournée pour le livreur numéro " + tournee.getLivreur().getNumero());
        ecrivain.println("Nombre de livraisons à effectuer : " + tournee.getLivraisons().size() + '\n');
        ecrivain.println("L'itinéraire à suivre est le suivant :");
        // Départ
        ecrivain.println("Départ de l'entrepôt ");
        for (int i = 0; i < tournee.getLivraisons().size(); ++i) {
            Itineraire itineraire = tournee.getItineraires().get(i);
            // Itinéraire entre deux livraisons
            for (int j = 0; j < itineraire.getIntersections().size() - 1; ++j) {
                Intersection intersectionCourante = itineraire.getIntersections().get(j);
                Intersection intersectionSuivante = itineraire.getIntersections().get(j + 1);
                String nomRue = intersectionCourante.getIntersections().get(intersectionSuivante).getValue();
                ecrivain.println("Prendre " + nomRue);
                if(j != itineraire.getIntersections().size() - 2){
                    ecrivain.println(", puis");
                }
            }

            // Affichage de la livraison
            Livraison livraison = tournee.getLivraisons().get(i);
            ecrivain.println("La livraison doit être livrée entre " + livraison.getFenetreDeLivraison());
            ecrivain.println("L'arrivée à la livraison numéro " + livraison.getNumero() +
                    " est prévue à " + livraison.getHeurePassage()); // TODO heure de passage heure d'arrivée

            // Départ prochaine livraison
            ecrivain.println("Départ du point de livraison prévu à " + livraison.getHeurePassage().plusMinutes(tournee.getTempsParLivraison()) + ".");
        }
        // Retour à l'entrepôt
        ecrivain.println("Retour à l'entrepôt.");
        Itineraire itineraire = tournee.getItineraires().get(tournee.getLivraisons().size());
        // Itinéraire entre deux livraisons
        for (int j = 0; j < itineraire.getIntersections().size() - 1; ++j) {
            Intersection intersectionCourante = itineraire.getIntersections().get(j);
            Intersection intersectionSuivante = itineraire.getIntersections().get(j + 1);
            String nomRue = intersectionCourante.getIntersections().get(intersectionSuivante).getValue();
            ecrivain.println("Prendre " + nomRue);
            if(j != itineraire.getIntersections().size() - 2){
                ecrivain.println(", puis");
            }
        }
        ecrivain.println("Arrivée à l'entrepôt.");
    }
}
