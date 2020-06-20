/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gmein
 */
public class Test {

    public static MonteCarloSimulation simulationTest(Group visualizations, Label progress) {
        Shape wallShape = new CubeFXyz(100);
        wallShape.getTransforms().add(new Translate(100,0,0));
        wallShape.setColor("blue");
        wallShape.setDrawMode(DrawMode.LINE);
        wallShape.setOpacity(0.1);
        Part wall = new Part("Wall1", wallShape, Unobtainium.getInstance());
        
        Shape wall2Shape = new CubeFXyz(400,20,400);
        wall2Shape.getTransforms().add(new Translate(0,100,0));
        wall2Shape.setColor("purple");
        wall2Shape.setDrawMode(DrawMode.LINE);
        wall2Shape.setOpacity(0.1);
        Part wall2 = new Part("Wall2", wall2Shape, Unobtainium.getInstance());

        Shape detectorShape = new CubeFXyz(50);
        detectorShape.getTransforms().add(new Translate(200, 0, 0));
        detectorShape.setColor("green");
        detectorShape.setOpacity(0.5);
        detectorShape.setDrawMode(DrawMode.LINE);
        Detector detector = new Detector("Detector 1", detectorShape);

        
        Util.Graphics.drawSphere(visualizations, Vector3D.ZERO, 5, "red");
        Util.Graphics.drawLine(visualizations, new Vector3D(-1000,0,0), new Vector3D(1000,0,0), Color.CYAN);
        Util.Graphics.drawLine(visualizations, new Vector3D(0, -1000,0), new Vector3D(0,1000,0), Color.YELLOW);
        Util.Graphics.drawLine(visualizations, new Vector3D(0,0,-1000), new Vector3D(0,0,1000), Color.RED);

        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(wall, wall2, detector);
        visualizations.getChildren().add(fusor);

        return new MonteCarloSimulation(fusor, Vector3D.ZERO, visualizations, 
                (p) -> Platform.runLater(() -> progress.setText("Complete: " + ((int)p) + " %")));
    }

    //
    // old stuff
    //
    public static Group test1() {
        Cube cube = new Cube(200);
        cube.setRotationAxis(new Point3D(1, 1, 1));
        cube.setTranslateX(100);
        cube.setTranslateY(100);
        cube.setTranslateZ(100);

        CubeFXyz cube2 = new CubeFXyz(100, 100, 100);
        cube2.setTranslateX(100);
        cube2.setTranslateY(100);
        cube2.setTranslateZ(100);
        cube2.setRotationAxis(new Point3D(-1, 1, 1));

        CubeOBJ cube3a = null;
        try {
            cube3a = new CubeOBJ();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Something went wrong loading the cube URL");
        }
        CubeOBJ cube3 = cube3a;
        cube3.setTranslateX(100);
        cube3.setTranslateY(100);
        cube3.setTranslateZ(100);
        cube3.setRotationAxis(new Point3D(-1, -1, 1));

        // little thread to keep rotating the cube
        Thread t = new Thread(() -> {
            try {
                for (;;) {
                    Thread.sleep(50);
                    // runLater() posts the argument lambda to the main event queue 
                    // really to run as soon as possible, but on the main thread.
                    Platform.runLater(() -> {
                        cube.setRotate(cube.getRotate() + 1);
                        cube2.setRotate(cube2.getRotate() + 1);
                        cube3.setRotate(cube3.getRotate() + 1);
                    });
                }
            } catch (InterruptedException ex) {
            }
        });
        t.start();

        //Creating a Group object  
        Group g = new Group(cube, cube2, cube3);

        return g;
    }

}
