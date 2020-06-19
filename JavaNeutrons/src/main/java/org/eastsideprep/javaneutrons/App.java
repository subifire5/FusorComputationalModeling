package org.eastsideprep.javaneutrons;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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

        // prepare 3d test for later
        MonteCarloSimulation sim = Test.simulationTest();
        SubScene sub = new SubScene(sim.assembly, 640, 480);
        Camera camera = new PerspectiveCamera();
        sub.setCamera(camera);

        Button b = new Button("Start simulation");
        b.setOnAction((e) -> {
            sim.simulateNeutrons(10);
        });
        b.setPrefWidth(200);

        Button b2 = new Button("Show results");
        b2.setOnAction((e) -> {
            Detector d = sim.assembly.detectors.get(0);
            root.setCenter(d.fluenceOverEnergy.makeChart("Fluence"));
        });
        b2.setPrefWidth(200);

        
        Button bTest = new Button("Show assembly");
        bTest.setOnAction((e) -> {
            root.setCenter(sub);
        });
        bTest.setPrefWidth(200);

        VBox buttons = new VBox();
        buttons.getChildren().addAll(b, b2, bTest);
        root.setLeft(buttons);

        var scene = new Scene(root, 640, 480);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            Test.processEvent(e, camera);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
