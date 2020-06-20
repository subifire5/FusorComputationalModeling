package org.eastsideprep.javaneutrons;

import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        // random stuff first
        Util.Math.random.setSeed(1234);
        
        
        root = new BorderPane();
        Label progress = new Label(""); // need to hand this to Test()

        // prepare sim for later
        Group visualizations = new Group();
        MonteCarloSimulation sim = Test.simulationTest(visualizations, progress);

        //camera in subscene
        SubScene sub = new SubScene(visualizations, 1500, 900);
        Camera camera = new PerspectiveCamera();
        camera.setRotationAxis(new Point3D(0, 1, 0));
        camera.setRotate(-20);
        camera.setRotationAxis(new Point3D(1, 0, 0));
        camera.setRotate(-20);
        camera.setTranslateX(-600);
        camera.setTranslateY(-500);
        sub.setCamera(camera);

        // control buttons and progress 
 
        Button b = new Button("Start simulation");
        b.setOnAction((e) -> {
            //
            // here is where we run the actual simulation
            // parameters are neutron count and callback for UI update (percent complete)
            //
            sim.simulateNeutrons(10);
        });
        b.setPrefWidth(200);

        Button b2 = new Button("Show stats");
        b2.setOnAction((e) -> {
            root.setCenter(sim.makeChart("Fluence"));
        });
        b2.setPrefWidth(200);

        Button bTest = new Button("Show assembly");
        bTest.setOnAction((e) -> {
            root.setCenter(sub);
        });
        bTest.setPrefWidth(200);

        VBox buttons = new VBox();
        buttons.getChildren().addAll(b, b2, bTest, progress);
        root.setLeft(buttons);
        
        // initial view
        root.setCenter(sub);

        // scene and keyboard controls
        var scene = new Scene(root, 1700, 900);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            processKeyEvent(e, camera);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    static void processKeyEvent(KeyEvent event, Camera camera) {
        switch (event.getCode()) {

            case PAGE_UP:
            case EQUALS:
                camera.translateZProperty().set(camera.getTranslateZ() + 10);
                break;
            case PAGE_DOWN:
            case MINUS:
                camera.translateZProperty().set(camera.getTranslateZ() - 10);
                break;

            case UP:
                camera.setRotationAxis(new Point3D(1, 0, 0));
                camera.setRotate(camera.getRotate() - 1);
                break;
            case LEFT:
                camera.setRotationAxis(new Point3D(0, 1, 0));
                camera.setRotate(camera.getRotate() - 1);
                break;
            case DOWN:
                camera.setRotationAxis(new Point3D(1, 0, 0));
                camera.setRotate(camera.getRotate() + 1);
                break;
            case RIGHT:
                camera.setRotationAxis(new Point3D(0, 1, 0));
                camera.setRotate(camera.getRotate() + 1);
                break;
        }
    }

}
