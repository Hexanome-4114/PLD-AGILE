import com.github.hexanome4114.pldagile.modele.Intersection;
import com.github.hexanome4114.pldagile.modele.Plan;
import com.github.hexanome4114.pldagile.utilitaire.Serialiseur;
import org.dom4j.DocumentException;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SerialiseurTest {

    @org.junit.jupiter.api.Test
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

        assertEquals(616, plan.getSegments().size());
    }
}
