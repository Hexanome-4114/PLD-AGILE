import com.github.hexanome4114.pldagile.controleur.Controleur;
import com.github.hexanome4114.pldagile.modele.FenetreDeLivraison;
import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Livraison;
import com.github.hexanome4114.pldagile.modele.Livreur;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import org.custommonkey.xmlunit.XMLUnit;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SerialiseurTest {

    @Test
    void chargerPlan() {
        URL url;
        Plan plan;

        try {
            url = this.getClass().getResource("/smallMap.xml");
            plan = Serialiseur.chargerPlan(new File(url.getFile()));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        Intersection entrepot = plan.getEntrepot();
        assertAll(
            () -> assertEquals(45.749790, entrepot.getLatitude()),
            () -> assertEquals(4.875720, entrepot.getLongitude())
        );

        assertEquals(308, plan.getIntersections().size());
    }

    @Test
    void sauvegarderLivraisons() {
        List<Livraison> livraisons = new ArrayList<>();

        FenetreDeLivraison fenetre = new FenetreDeLivraison(8, 9);
        Livreur livreur = new Livreur(1, Controleur.VITESSE_MOYENNE);

        livraisons.add(new Livraison(1, fenetre, livreur,
                new Intersection("1", 45.759335, 4.877039)
        ));

        livraisons.add(new Livraison(3, fenetre, livreur,
                new Intersection("2", 45.756004, 4.8742256)
        ));

        try {
            File fichierAttendu = new File(this.getClass()
                    .getResource("/livraisons.xml").getFile());
            File fichierResultat = File.createTempFile("temp", ".xml");

            Serialiseur.sauvegarderLivraisons(fichierResultat, livraisons);

            XMLUnit.setIgnoreWhitespace(true);
            assertXMLEqual(new FileReader(fichierAttendu),
                    new FileReader(fichierResultat));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void chargerLivraisons() {
        File fichier = new File(this.getClass()
                .getResource("/livraisons.xml").getFile());

        List<Livraison> livraisons = null;
        try {
            livraisons = Serialiseur.chargerLivraisons(fichier);

            assertEquals(2, livraisons.size());

            // TODO comparer les livraisons
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
