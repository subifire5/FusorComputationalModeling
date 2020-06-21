/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.assemblies.Assembly;
import org.eastsideprep.javaneutrons.assemblies.Detector;
import org.eastsideprep.javaneutrons.assemblies.Part;
import org.eastsideprep.javaneutrons.core.MonteCarloSimulation;
import org.eastsideprep.javaneutrons.core.Util;
import org.eastsideprep.javaneutrons.materials.HumanBodyMaterial;
import org.eastsideprep.javaneutrons.materials.Paraffin;
import org.eastsideprep.javaneutrons.materials.Steel;
import org.eastsideprep.javaneutrons.materials.Vacuum;
import org.eastsideprep.javaneutrons.shapes.Cuboid;
import org.eastsideprep.javaneutrons.shapes.HumanBody;
import org.eastsideprep.javaneutrons.shapes.Shape;
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

        Util.Graphics.drawCoordSystem(visualizations);

        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(wall, wall2, detector);
        visualizations.getChildren().add(fusor.getGroup());

        return new MonteCarloSimulation(fusor, Vector3D.ZERO);
    }

    public static MonteCarloSimulation simulationTest2(Group visualizations) {
        //
        // Wall1
        // this cube-shaped wall is loaded from an obj file in resources
        // any obj files need to live their (folder src/main/resources in folder view)
        //

        double gap = 3; // in cm
        double offset = 2 * gap; // in cm

        //
        // the upper plate is a Cuboid from a hand-written mesh
        //
        Shape wall1Shape = new Cuboid(100, 20, 100);
        // move plate up
        wall1Shape.getTransforms().add(new Translate(100, -10 - offset - gap, 0));
        wall1Shape.setColor("purple");
        wall1Shape.setDrawMode(DrawMode.LINE);
        wall1Shape.setOpacity(0.1);
        Part wall1 = new Part("Wall2", wall1Shape, Paraffin.getInstance());

        //
        // the lower plate is a Cuboid from a hand-written mesh
        //
        Shape wall2Shape = new Cuboid(100, 20, 100);
        // move plate down
        wall2Shape.getTransforms().add(new Translate(100, 10 - offset, 0));
        wall2Shape.setColor("purple");
        wall2Shape.setDrawMode(DrawMode.LINE);
        wall2Shape.setOpacity(0.1);
        Part wall2 = new Part("Wall2", wall2Shape, Paraffin.getInstance());

        //
        // The detector is made from a stock - FXyz CuboidMesh
        //
        double s = 20;
        Shape detectorShape = new Shape(new CuboidMesh(s, 3 * s, 5 * s));
        // move detector behind cube wall
        detectorShape.getTransforms().add(new Translate(200, 0, 0));
        detectorShape.setColor("green");
        detectorShape.setOpacity(0.5);
        detectorShape.setDrawMode(DrawMode.LINE);
        Detector detector = new Detector("Detector 1", detectorShape, Steel.getInstance());

        Util.Graphics.drawCoordSystem(visualizations);

        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(wall1, wall2, detector);
        visualizations.getChildren().add(fusor.getGroup());

        return new MonteCarloSimulation(fusor, Vector3D.ZERO);
    }

    public static MonteCarloSimulation simulationTest3(Group visualizations) {
        //
        // Wall1
        // this cube-shaped wall is loaded from an obj file in resources
        // any obj files need to live their (folder src/main/resources in folder view)
        //

        double gap = 3; // in cm
        double offset = 2 * gap; // in cm
        //
        // igloo
        //
        Assembly igloo = new Assembly("igloo", Test.class.getResource("/igloo.obj"), Paraffin.class);

        //
        // The detector is made from a stock - FXyz CuboidMesh
        //
        double s = 20;
        Shape detectorShape = new Shape(new CuboidMesh(s, 3 * s, 5 * s));
        // move detector behind cube wall
        detectorShape.getTransforms().add(new Translate(200, 0, 0));
        Detector detector = new Detector("Detector 1", detectorShape, Vacuum.getInstance());

        //
        // body
        //
        Shape bodyShape = new HumanBody();
        bodyShape.getTransforms().add(new Translate(0, 0, -200));
        Detector body = new Detector("Body", bodyShape, HumanBodyMaterial.getInstance());

        
        // assemble the Fusor out of the other stuff
        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(igloo, detector, body);
        
        // ubt it all into the visual scene
        Util.Graphics.drawCoordSystem(visualizations);
        visualizations.getChildren().add(fusor.getGroup());

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

        Shape cube2 = new Shape(new CuboidMesh(100, 100, 100));
        cube2.setTranslateX(100);
        cube2.setTranslateY(100);
        cube2.setTranslateZ(100);
        cube2.setRotationAxis(new Point3D(-1, 1, 1));

        Shape cube3 = new Shape(Test.class.getResource("/cube.obj"));
        cube3.setTranslateX(100);
        cube3.setTranslateY(100);
        cube3.setTranslateZ(100);
        cube3.setRotationAxis(new Point3D(-1, -1, 1));

        Shape body = new Shape(Test.class.getResource("/body.obj"));
        body.setScaleX(100);
        body.setScaleY(100);
        body.setScaleZ(100);

        body.setTranslateX(200);
        body.setTranslateY(200);
        body.setTranslateZ(200);
        body.setRotationAxis(new Point3D(-1, -1, -1));
        System.out.println("Volume:" + body.getVolume());
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
                        body.setRotate(cube3.getRotate() + 1);
                    });
                }
            } catch (InterruptedException ex) {
            }
        });
        t.start();

        //Creating a Group object  
        Group g = new Group(cube, cube2, cube3, body);

        return g;
    }

}
