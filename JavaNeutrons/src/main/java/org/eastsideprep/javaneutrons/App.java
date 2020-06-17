package org.eastsideprep.javaneutrons;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    BorderPane root;
    static Random random = new Random();

    @Override
    public void start(Stage stage) {
        root = new BorderPane();

        Button b = new Button("Start simulation");
        b.setOnAction((e) -> {
            // from 10^-10 to 10^5 with 50 bins
            LogHistogram spectrum = new LogHistogram(-10, 10, 50);

            // create 100000 random values and put them in
            for (int i = 0; i < 100000; i++) {
                spectrum.record(random.nextDouble() * 1E10);
            }

            root.setCenter(spectrum.makeChart(true));
        });

        root.setLeft(b);
        var scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
