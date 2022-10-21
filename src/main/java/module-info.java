module com.github.hexanome4114.pldagile.agile {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.hexanome4114.pldagile to javafx.fxml;
    exports com.github.hexanome4114.pldagile;
}