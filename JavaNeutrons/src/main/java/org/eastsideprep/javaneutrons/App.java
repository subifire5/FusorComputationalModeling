package org.eastsideprep.javaneutrons;

import java.util.Random;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
        Group group3D = Test3D.test();
        SubScene sub = new SubScene(group3D, 640, 480);
        Camera camera = new PerspectiveCamera();
        sub.setCamera(camera);

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

        Button bTest = new Button("3D test");
        bTest.setOnAction((e) -> {
            root.setCenter(sub);
        });

        VBox buttons = new VBox();
        buttons.getChildren().addAll(b, bTest);
        root.setLeft(buttons);

        var scene = new Scene(root, 640, 480);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (e)->{
            Test3D.processEvent(e, camera);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
