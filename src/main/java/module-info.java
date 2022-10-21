module com.github.hexanome4114.pldagile.agile {
    requires javafx.controls;
    requires javafx.fxml;
    requires dom4j;


    opens com.github.hexanome4114.pldagile to javafx.fxml;
    exports com.github.hexanome4114.pldagile;
    exports com.github.hexanome4114.pldagile.controleur;
    opens com.github.hexanome4114.pldagile.controleur to javafx.fxml;
    exports com.github.hexanome4114.pldagile.modele;
    opens com.github.hexanome4114.pldagile.modele to javafx.fxml;
}