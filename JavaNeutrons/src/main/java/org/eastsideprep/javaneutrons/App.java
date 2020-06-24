package org.eastsideprep.javaneutrons;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.eastsideprep.javaneutrons.core.MonteCarloSimulation;
import org.eastsideprep.javaneutrons.core.Util;

/**
 * JavaFX App
 */
public class App extends Application {

    BorderPane root;
    HBox view;
    MonteCarloSimulation sim;
    Group visGroup;
    LinkedTransferQueue<Node> visQueue;

    Button bRun;
    Button bRunSV;
    Button bRunET;

    Label progress;

    @Override
    public void start(Stage stage) {
        // random stuff first
        Util.Math.random.setSeed(1234);

        root = new BorderPane();
        progress = new Label(""); // need to hand this to Test()

        // prepare sim for later
        visQueue = new LinkedTransferQueue<>();
        visGroup = new Group();
        sim = Test.simulationTest(visQueue);
        visQueue.drainTo(visGroup.getChildren());

        // control buttons and progress 
        TextField tf = new TextField("200");
        tf.setPrefWidth(200);

        bRun = new Button("Start simulation - GM");
        bRunSV = new Button("Start simulation - SV");
        bRunET = new Button("Start simulation - ET");

        bRun.setOnAction((e) -> {
            sim = Test.simulationTest(visQueue);
            this.runSim(Long.parseLong(tf.getText()));
        });
        bRun.setPrefWidth(200);

        bRunSV.setOnAction((e) -> {
            sim = TestSV.simulationTest(visQueue);
            this.runSim(Long.parseLong(tf.getText()));
        });
        bRunSV.setPrefWidth(200);

        bRunET.setOnAction((e) -> {
            sim = TestET.simulationTest(visQueue);
            this.runSim(Long.parseLong(tf.getText()));
        });
        bRunET.setPrefWidth(200);

        Button bStats = new Button("Show entry counts");
        bStats.setOnAction((e) -> {
            root.setCenter(sim.makeChart("Body", "EntryCounts"));
        });
        bStats.setPrefWidth(200);

        Button bStatsFluence = new Button("Show fluence");
        bStatsFluence.setOnAction((e) -> {
            root.setCenter(sim.makeChart("Body", "Fluence"));
        });
        bStatsFluence.setPrefWidth(200);

        Button bStatsEnv = new Button("Show escapes");
        bStatsEnv.setOnAction((e) -> {
            root.setCenter(sim.makeChart(null, null));
        });
        bStatsEnv.setPrefWidth(200);

        Button bView = new Button("Show assembly");
        bView.setOnAction((e) -> {
            root.setCenter(view);
        });
        bView.setPrefWidth(200);

        Button bTest = new Button("Test visuals");
        bTest.setOnAction((e) -> {
            root.setCenter(Test.testVisuals());
        });
        bTest.setPrefWidth(200);

        VBox buttons = new VBox();
        buttons.getChildren().addAll(tf, bRun, bRunSV, bRunET, bStats, bStatsFluence, bStatsEnv, bView, bTest, progress);
        root.setLeft(buttons);

        // create camera control, set scene and stage
        CameraControl mainScene = new CameraControl(1500, 900);
        view = mainScene.outer;
        mainScene.root.getChildren().add(visGroup);
        root.setCenter(view);
        var scene = new Scene(root, 1700, 900);
        //scene.setOnKeyPressed((ex) -> mainScene.controlCamera(ex));
        scene.setOnKeyPressed((ex) -> mainScene.handleKeyPress(ex));
        scene.setOnMouseDragged((ex) -> mainScene.handleDrag(ex));
        scene.setOnMousePressed((ex) -> mainScene.handleClick(ex));
        scene.setOnScroll((ex) -> mainScene.handleScroll(ex));

        // scene and keyboard controls (old)
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            mainScene.handleKeyPress(e);
        });
        stage.setScene(scene);
        stage.show();

    }

    public void runSim(long count) {
        visGroup.getChildren().clear();
        if (this.sim == null) {
            return;
        }

        //
        // here is where we run the actual simulation
        //
        bRun.setDisable(true);
        bRunSV.setDisable(true);
        bRunET.setDisable(true);
        progress.setText("Complete: 0 %");
        Group p = new Group();

        final Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1),
                (e) -> {
                    progress.setText("Complete: " + Math.round(100 * sim.completed.get() / count) + " %");
                    visQueue.drainTo(visGroup.getChildren());
                    if (sim.completed.get() == count) {
                        tl.stop();
                        bRun.setDisable(false);
                        bRunET.setDisable(false);
                        bRunSV.setDisable(false);
                        progress.setText("Complete: 100 %");
                    }
                }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();

        sim.simulateNeutrons(count);
        visQueue.drainTo(visGroup.getChildren());

        root.setCenter(view);
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
