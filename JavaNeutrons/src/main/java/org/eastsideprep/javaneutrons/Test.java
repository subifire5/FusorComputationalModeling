/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.materials.Paraffin;
import org.eastsideprep.javaneutrons.materials.Steel;
import org.eastsideprep.javaneutrons.materials.Unobtainium;
import org.fxyz3d.shapes.primitives.CuboidMesh;

/**
 *
 * @author gmein
 */
public class Test {

    public static MonteCarloSimulation simulationTest(Group visualizations) {
        //
        // Wall1
        // this cube-shaped wall is loaded from an obj file in resources
        // any obj files need to live their (folder src/main/resources in folder view)
        //
        // this file is a cube of sidelength 100
        Shape wallShape = new Shape(Test.class.getResource("/cube.obj"));
        // this obj file has the origin at a corner - need to translate to center
        wallShape.getTransforms().add(new Translate(-50, -50, -50));
        // and then move along the x-axis to where we want it
        wallShape.getTransforms().add(new Translate(100, 0, 0));
        wallShape.setColor("blue");
        wallShape.setDrawMode(DrawMode.LINE);
        wallShape.setOpacity(0.1);
        Part wall = new Part("Wall1", wallShape, Paraffin.getInstance());

        //
        // the lower plate is a Cuboid from a hand-written mesh
        //
        double s = 50;
        Shape wall2Shape = new Cuboid(500, 20, 500);
        // move plate down
        wall2Shape.getTransforms().add(new Translate(0, 100, 0));
        wall2Shape.setColor("purple");
        wall2Shape.setDrawMode(DrawMode.LINE);
        wall2Shape.setOpacity(0.1);
        Part wall2 = new Part("Wall2", wall2Shape, Paraffin.getInstance());

        //
        // The detector is made from a stock - FXyz CuboidMesh
        //
        Shape detectorShape = new Shape(new CuboidMesh(s, s, s));
        // move detector behind cube wall
        detectorShape.getTransforms().add(new Translate(200, 0, 0));
        detectorShape.setColor("green");
        detectorShape.setOpacity(0.5);
        detectorShape.setDrawMode(DrawMode.LINE);
        Detector detector = new Detector("Detector 1", detectorShape, Steel.getInstance());

        Util.Graphics.drawSphere(visualizations, Vector3D.ZERO, 5, "red");
        Util.Graphics.drawLine(visualizations, new Vector3D(-1000, 0, 0), new Vector3D(1000, 0, 0), Color.CYAN);
        Util.Graphics.drawLine(visualizations, new Vector3D(0, -1000, 0), new Vector3D(0, 1000, 0), Color.YELLOW);
        Util.Graphics.drawLine(visualizations, new Vector3D(0, 0, -1000), new Vector3D(0, 0, 1000), Color.RED);

        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(wall, wall2, detector);
        visualizations.getChildren().add(fusor);

        return new MonteCarloSimulation(fusor, Vector3D.ZERO);
    }

    //
    // old stuff - Sydney, this is mostly for visual testing. Return a group you can add in main.
    //
    public static Group test1() {
        Cuboid cube = new Cuboid(200);
        cube.setRotationAxis(new Point3D(1, 1, 1));
        cube.setTranslateX(100);
        cube.setTranslateY(100);
        cube.setTranslateZ(100);

        CubeFXyz cube2 = new CubeFXyz(100, 100, 100);
        cube2.setTranslateX(100);
        cube2.setTranslateY(100);
        cube2.setTranslateZ(100);
        cube2.setRotationAxis(new Point3D(-1, 1, 1));

        Shape cube3 = new Shape(Test.class.getResource("/cube.obj"));
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
