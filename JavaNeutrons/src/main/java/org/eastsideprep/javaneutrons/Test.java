/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gmein
 */
public class Test {

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
    
    
    public static MonteCarloSimulation simulationTest() {
        Shape wallShape = new Cube(20);
        wallShape.setTranslateX(10);
        Part wall = new Part("Wall", wallShape, Unobtainium.getInstance());
        
        Shape detectorShape = new Cube(20);
        detectorShape.setTranslateX(20);
        Detector detector = new Detector("Detector 1", new Vector3D(20,0,0), 3);
        
        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(wall, detector);
        
        return new MonteCarloSimulation(fusor, Vector3D.ZERO);
    }

    static void processEvent(KeyEvent event, Camera camera) {
        switch (event.getCode()) {

            case PAGE_UP:
                camera.translateZProperty().set(camera.getTranslateZ() + 10);
                break;
            case PAGE_DOWN:
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
