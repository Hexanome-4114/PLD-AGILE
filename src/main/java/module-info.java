module com.github.hexanome4114.pldagile.agile {
    requires javafx.controls;
    requires javafx.fxml;
    requires dom4j;
    requires jaxen;
    requires java.desktop;


    opens com.github.hexanome4114.pldagile to javafx.fxml;
    exports com.github.hexanome4114.pldagile;
    exports com.github.hexanome4114.pldagile.controleur;
    opens com.github.hexanome4114.pldagile.controleur to javafx.fxml;
    exports com.github.hexanome4114.pldagile.utilitaire;
    opens com.github.hexanome4114.pldagile.utilitaire to javafx.fxml;
    exports com.github.hexanome4114.pldagile.modele;
}