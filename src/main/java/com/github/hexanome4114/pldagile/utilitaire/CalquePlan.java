package com.github.hexanome4114.pldagile.utilitaire;

import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.gluonhq.maps.MapLayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.Map;

/**
 * Calque du plan contenant les intersections.
 */
public final class CalquePlan extends MapLayer {

    private static final int TAILLE_POINT = 4;

    private static final Color COULEUR_POINT = Color.RED;

    private static final Image IMAGE_ENTREPOT = new Image(
            CalquePlan.class.getResource("/images/entrepot.png").toString(),
            25, 25, false, false);

    private static final Image IMAGE_POINT_SELECTIONNE = new Image(
            CalquePlan.class.getResource("/images/pin.png").toString());

    /**
     * Map contenant les points de livraison et leur Node associé.
     */
    private final ObservableMap<Intersection, Circle> points =
            FXCollections.observableHashMap();

    /**
     * Map contenant les livraisons et leur Node associé.
     */
    private final ObservableMap<Livraison, Node> livraisons =
            FXCollections.observableHashMap();

    /**
     * Point de livraison sélectionné par l'utilisateur.
     */
    private Circle pointSelectionne;

    /**
     * Point particulier représentant l'entrepôt.
     */
    private Pair<Intersection, ImageView> entrepot;

    public CalquePlan() { }

    /**
     * Ajoute un point sur le calque.
     * @param point
     */
    public void ajouterPoint(final Intersection point) {
        Circle cercle = new Circle(TAILLE_POINT, COULEUR_POINT);
        cercle.setVisible(false); // les points sont masqués par défaut
        cercle.setCursor(Cursor.HAND);

        points.put(point, cercle);
        this.getChildren().add(cercle);
        this.markDirty();
    }

    /**
     * Ajoute une livraison sur le calque.
     * @param livraison
     */
    public void ajouterLivraison(final Livraison livraison) {
        Shape forme = this.getForme(livraison.getFenetreDeLivraison());
        forme.setFill(this.getCouleur(livraison.getLivreur()));

        Text texte = new Text(String.valueOf(livraison.getNumero()));
        texte.setFill(Color.WHITE);
        texte.setStyle("-fx-font-size: " + (15 - 2 * texte.getText().length()));

        StackPane stack = new StackPane();
        stack.getChildren().addAll(forme, texte);

        livraisons.put(livraison, stack);
        this.getChildren().add(stack);
        this.markDirty();
    }

    /**
     * Enlève une livraison du calque.
     * @param livraison
     */
    public void enleverLivraison(final Livraison livraison) {
        Node noeud = livraisons.get(livraison);
        livraisons.remove(livraison);
        this.getChildren().remove(noeud);
        this.markDirty();
    }

    public void setEntrepot(final Intersection entrepot) {
        ImageView imageView = new ImageView(IMAGE_ENTREPOT);
        this.entrepot = new Pair(entrepot, imageView);
        this.getChildren().add(imageView);
        this.markDirty();
    }

    public void setPointSelectionne(final Intersection intersection) {
        Circle point = points.get(intersection);

        if (this.pointSelectionne != null) {
            // réinitialisation si une sélection a déjà eu lieu
            this.pointSelectionne.setFill(COULEUR_POINT);
            this.pointSelectionne.setRadius(TAILLE_POINT);
            this.pointSelectionne.setCenterY(0);
            this.pointSelectionne.setVisible(point.isVisible());
        }

        point.setFill(new ImagePattern(IMAGE_POINT_SELECTIONNE));
        point.setRadius(15);
        // modification du Y pour bien positionner le pin
        point.setCenterY(point.getCenterY() - point.getRadius());
        point.setVisible(true);

        this.pointSelectionne = point;
    }

    /**
     * Affiche ou masque les points du calque.
     * @param visible
     */
    public void afficherPoints(final boolean visible) {
        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            Circle cercle = point.getValue();

            // on ne masque jamais le point sélectionné
            if (cercle != this.pointSelectionne) {
                cercle.setVisible(visible);
            }
        }
    }

    /**
     * Retourne le point du calque le plus proche d'un point donné.
     * @param x
     * @param y
     * @return le point du calque le plus proche
     */
    public Intersection trouverPointPlusProche(
            final double x, final double y) {
        if (points.isEmpty()) {
            return null;
        }

        // initialisation
        Intersection pointPlusProche = points.keySet().stream().findFirst()
                .get();
        double distanceMin = this.calculerDistanceEuclidienne(x, y,
                points.get(pointPlusProche).getTranslateX(),
                points.get(pointPlusProche).getTranslateY());

        // calcul de la distance pour chaque point
        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            double distance = this.calculerDistanceEuclidienne(x, y,
                    point.getValue().getTranslateX(),
                    point.getValue().getTranslateY());

            if (distance < distanceMin) {
                distanceMin = distance;
                pointPlusProche = point.getKey();
            }
        }

        return pointPlusProche;
    }

    /**
     * Retourne la distance euclidienne entre deux points.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return la distance euclidienne entre deux points
     */
    private double calculerDistanceEuclidienne(final double x1, final double y1,
            final double x2, final double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return diffX * diffX + diffY * diffY;
    }

    /**
     * Détermine la forme à afficher pour la livraison en fonction de la
     * fenêtre de livraison.
     * @param fenetre la fenêtre de livraison de la livraison
     * @return la forme à afficher
     */
    private Shape getForme(final FenetreDeLivraison fenetre) {
        Shape forme;
        switch (fenetre) {
            case H8_H9:
                forme = new Circle(8);
                break;
            case H9_H10:
                Polygon triangle = new Polygon(0, 0, 9, -18, -9, -18);
                triangle.setRotate(180);

                forme = triangle;
                break;
            case H10_H11:
                forme = new Rectangle(16, 16);
                break;
            case H11_H12:
                Polygon pentagone = new Polygon();
                for (int i = 0; i < 5; i++) {
                    pentagone.getPoints().add(10 * Math.sin(
                            2 * i * Math.PI / 5));
                    pentagone.getPoints().add(10 * Math.cos(
                            2 * i * Math.PI / 5));
                }
                pentagone.setRotate(180 / 5);

                forme = pentagone;
                break;
            default:
                forme = new Circle(8);
        }
        return forme;
    }

    private Color getCouleur(final Livreur livreur) {
        Color couleur;
        switch (livreur) {
            case LIVREUR_1:
                couleur = Color.BLUE;
                break;
            case LIVREUR_2:
                couleur = Color.GREEN;
                break;
            case LIVREUR_3:
                couleur = Color.GREY;
                break;
            case LIVREUR_4:
                couleur = Color.ORANGE;
                break;
            default:
                couleur = Color.BLUE;
        }
        return couleur;
    }

    @Override
    protected void layoutLayer() {
        for (Map.Entry<Intersection, Circle> point : points.entrySet()) {
            positionner(point.getKey(), point.getValue());
        }

        for (Map.Entry<Livraison, Node> livraison : livraisons.entrySet()) {
            positionner(livraison.getKey().getAdresse(), livraison.getValue());
        }

        positionner(entrepot.getKey(), entrepot.getValue());
    }

    private void positionner(final Intersection intersection,
                             final Node noeud) {
        Point2D mapPoint = getMapPoint(intersection.getLatitude(),
                intersection.getLongitude());
        noeud.setTranslateX(mapPoint.getX());
        noeud.setTranslateY(mapPoint.getY());
    }
}
