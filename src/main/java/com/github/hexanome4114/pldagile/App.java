package com.github.hexanome4114.pldagile;

import com.github.hexanome4114.pldagile.controleur.Controleur;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class App extends Application {

    /** Largeur fenêtre. */
    private static final int LARGEUR = 320;

    /** Hauteur fenêtre. */
    private static final int HAUTEUR = 240;

    @Override
    public void start(final Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class
                .getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), LARGEUR, HAUTEUR);
        stage.setTitle("PLD-AGILE");
        stage.setScene(scene);

        Controleur controleur = new Controleur();
        controleur.setStage(stage);

        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
