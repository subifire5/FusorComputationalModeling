/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.core.Assembly;
import org.eastsideprep.javaneutrons.core.Isotope;
import org.eastsideprep.javaneutrons.core.Part;
import org.eastsideprep.javaneutrons.core.Event;
import org.eastsideprep.javaneutrons.core.EnergyEVHistogram;
import org.eastsideprep.javaneutrons.core.MonteCarloSimulation;
import org.eastsideprep.javaneutrons.core.Neutron;
import org.eastsideprep.javaneutrons.core.Util;
import org.eastsideprep.javaneutrons.materials.E12C;
import org.eastsideprep.javaneutrons.materials.E1H;
import org.eastsideprep.javaneutrons.materials.Paraffin;
import org.eastsideprep.javaneutrons.shapes.Cuboid;
import org.eastsideprep.javaneutrons.shapes.HumanBody;
import org.eastsideprep.javaneutrons.core.Shape;
import org.fxyz3d.shapes.primitives.CuboidMesh;

/**
 *
 * @author gmein
 */
public class TestGM {

    public static MonteCarloSimulation simulationTest(Group visualizations) {

//        LogHistogram test = new LogHistogram();
//        test.record(1000, 0.5e-6);
//        test.record(2000, 1e-6);
//        test.record(2100, 1.1e-6);
//        test.record(2500, 1.5e-6);
//        
//        test.record(3000, 0.5e7);
//        test.record(4000, 1e7);
//        test.record(5000, 2e7);
//        test.makeSeries("",1);
//        
        return simulationTestWhitmer(visualizations);
    }

    public static XYChart.Series customTest(boolean log, boolean xOnly) {
        EnergyEVHistogram test = new EnergyEVHistogram();
        Neutron n = new Neutron(Vector3D.ZERO, Util.Math.randomDir(), Neutron.startingEnergyDD, false);
        Event event = new Event(Vector3D.ZERO, Event.Code.Scatter);
        event.element = E1H.getInstance();

        for (int i = 0; i < 100000; i++) {
            n.setDirectionAndEnergy(xOnly ? Vector3D.PLUS_I : Util.Math.randomDir(), Neutron.startingEnergyDD);
            n.setPosition(Vector3D.ZERO);
            n.processEvent(event);
            test.record(1, event.energyOut);
        }

        return test.makeSeries("test", 1, log);
    }

    public static MonteCarloSimulation simulationTestSmoosh(Group visualizations) {

        double gap = 1;

        // vac chamber
        Part vacChamber = new Part("Vacuum chamber", new Shape(TestGM.class.getResource("/meshes/vac_chamber.obj")), "Steel");

        Shape iglooShape = new Shape(TestGM.class.getResource("/smoosh.obj"), "cm");
        iglooShape.getTransforms().add(0, new Rotate(90, new Point3D(1, 0, 0)));
        iglooShape.getTransforms().add(0, new Translate(0, 63, 0));
        iglooShape.setColor("silver");
        Part igloo = new Part("Wall1", iglooShape, "Paraffin");

        Shape detector1Shape = new Shape(new CuboidMesh(2, 100, 100));
        detector1Shape.getTransforms().add(0, new Translate(-(100 + 1), 0, 0));
        detector1Shape.setColor("pink");
        Part detector1 = new Part("Left detector", detector1Shape, "Vacuum");

        Shape detector2Shape = new Shape(new CuboidMesh(2, 100, 100));
        detector2Shape.getTransforms().add(0, new Rotate(90, new Point3D(0, 0, 1)));
        detector2Shape.getTransforms().add(0, new Translate(0, 100 + 1, 0));
        detector2Shape.setColor("pink");
        Part detector2 = new Part("Bottom detector", detector2Shape, "Vacuum");

        Shape detector3Shape = new Shape(new CuboidMesh(2, 100, 100));
        detector3Shape.getTransforms().add(0, new Rotate(90, new Point3D(0, 1, 0)));
        detector3Shape.getTransforms().add(0, new Translate(0, 0, 100 + 1));
        detector3Shape.setColor("pink");
        Part detector3 = new Part("Back detector", detector3Shape, "Vacuum");

        // assemble the Fusor out of the other stuff
        Assembly smoosh = new Assembly("Smooshed igloo");
        smoosh.addAll(igloo, vacChamber, detector1, detector2, detector3);

        // make some axes
        Util.Graphics.drawCoordSystem(visualizations);

        return new MonteCarloSimulation(smoosh, Vector3D.ZERO, visualizations);
    }

    public static MonteCarloSimulation simulationTestWhitmer(Group visualizations) {

        Paraffin.getInstance();
        Shape blockShape = new Shape(new CuboidMesh(025, 100, 100));
        Part wall = new Part("Block", blockShape, "Paraffin");
        wall.getTransforms().add(new Translate(50 + 12.5, 0, 0));
        wall.setColor("silver");

        Shape detectorShape = new Shape(new CuboidMesh(2, 100, 100));
        Part detector1 = new Part("Detector behind block", detectorShape, "Vacuum");
        detector1.getTransforms().add(new Translate(100 + 1, 0, 0));
        detector1.setColor("pink");

        Part detector2 = new Part("Detector opposite block", detectorShape, "Vacuum");
        detector2.getTransforms().add(new Translate(-(100 + 1), 0, 0));
        detector2.setColor("pink");

          // vac chamber
        Part vacChamber = new Part("Vacuum chamber", new Shape(TestGM.class.getResource("/meshes/vac_chamber.obj")), "Steel");
      
        // assemble the Fusor out of the other stuff
        Assembly whitmer = new Assembly("Whitmer");
        whitmer.addAll(wall, detector1, detector2, vacChamber);
        //whitmer.addTransform(new Translate(0, -100, 0));

        whitmer.containsMaterialAt("Vacuum", Vector3D.ZERO);

        MonteCarloSimulation mcs = new MonteCarloSimulation(whitmer, Vector3D.ZERO, visualizations);
        //mcs.xOnly = true;
        return mcs;
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
        // igloo
        //
        Assembly igloo = new Assembly("igloo", TestGM.class.getResource("/meshes/igloo.obj"), "Paraffin");
        //System.out.println("Macroscopic total cross-section for paraffin: "+Paraffin.getInstance().getSigma(1*Util.Physics.eV));
        //
        // The detector is made from a stock - FXyz CuboidMesh
        //
        double s = 20;
        Shape detectorShape = new Shape(new CuboidMesh(s, 3 * s, 5 * s));
        // move detector behind cube wall
        detectorShape.getTransforms().add(new Translate(200, 0, 0));
        Part detector = new Part("Detector 1", detectorShape, "Vacuum");

        //
        // body
        //
        Shape bodyShape = new HumanBody();
        //bodyShape.getTransforms().add(0,new Rotate(90, new Point3D(1,0,0)));
        bodyShape.getTransforms().add(0, new Translate(0, 0, -200));
        Part body = new Part("Body", bodyShape, "HumanBodyMaterial");

        // vac chamber
        Part vacChamber = new Part("Vacuum chamber", new Shape(TestGM.class.getResource("/meshes/vac_chamber.obj")), "Steel");

        // assemble the Fusor out of the other stuff
        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(igloo, detector, body, vacChamber);
        fusor.containsMaterialAt("Vacuum", Vector3D.ZERO);

        // make some axes
        Util.Graphics.drawCoordSystem(visualizations);

        return new MonteCarloSimulation(fusor, Vector3D.ZERO, visualizations);
    }

    public static Group testVisuals() {
        Group g = new Group();

        Sphere s = new Sphere(1000);
        Util.Graphics.setColor(s, "gray");
        s.setOpacity(0.1);
        s.setTranslateX(-500);
        s.setDrawMode(DrawMode.LINE);
        Vector3D orig = new Vector3D(200, 0, 0);

        for (int i = 0; i < 10000; i++) {
            // Vector3D v = Util.Math.randomDir().scalarMultiply(100);
            Vector3D dir = Util.Math.randomDir();
            double t = Util.Math.raySphereIntersect(orig, dir, new Vector3D(-500, 0, 0), 1000);
            if (t >= 0) {
                Vector3D v = orig.add(dir.scalarMultiply(t));
                Sphere p = new Sphere(5);
                Util.Graphics.setColor(p, "red");
                p.getTransforms().add(new Translate(v.getX(), v.getY(), v.getZ()));
                g.getChildren().add(p);
            }
        }
        Util.Graphics.drawCoordSystem(g);
        g.getChildren().add(s);
        return g;
    }

    //
    // old stuff - Sydney, this is mostly for visual testing. Return a group you can add in main.
    //
    public static Group testVisuals2() {
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

        Shape cube3 = new Shape(TestGM.class.getResource("/meshes/cube.obj"));
        cube3.setTranslateX(100);
        cube3.setTranslateY(100);
        cube3.setTranslateZ(100);
        cube3.setRotationAxis(new Point3D(-1, -1, 1));

        Shape body = new Shape(TestGM.class.getResource("/meshes/body.obj"));
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
