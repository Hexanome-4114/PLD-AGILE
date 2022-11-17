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
        try {
            URL urlPlan = this.getClass().getResource("/smallMap.xml");
            Plan plan = Serialiseur.chargerPlan(new File(urlPlan.getFile()));

            List<Livraison> livraisons = new ArrayList<>();

            livraisons.add(new Livraison(1, FenetreDeLivraison.H8_H9,
                    Livreur.LIVREUR_1, plan.getIntersections().get("33066088"))
            );

            livraisons.add(new Livraison(3, FenetreDeLivraison.H8_H9,
                    Livreur.LIVREUR_3, plan.getIntersections().get("55475004"))
            );

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
        try {
            URL urlPlan = this.getClass().getResource("/smallMap.xml");
            Plan plan = Serialiseur.chargerPlan(new File(urlPlan.getFile()));

            File fichier = new File(this.getClass()
                    .getResource("/livraisons.xml").getFile());

            List<Livraison> livraisons = Serialiseur.chargerLivraisons(fichier,
                    plan);

            assertEquals(2, livraisons.size());

            assertAll(
                    () -> assertEquals("Livraison{numero=1, fenetreDe"
                            + "Livraison=8h-9h, livreur=Livreur 1, adresse="
                            + "Intersection{33066088}}", livraisons.get(0)
                            .toString()),
                    () -> assertEquals("Livraison{numero=3, fenetreDe"
                            + "Livraison=8h-9h, livreur=Livreur 3, adresse="
                            + "Intersection{55475004}}", livraisons.get(1)
                            .toString())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
