/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.assemblies.Assembly;
import org.eastsideprep.javaneutrons.assemblies.Part;

/**
 *
 * @author gunnar
 */
public class MonteCarloSimulation {

    static boolean parallel = true;

    public interface ProgressLambda {

        void reportProgress(int p);
    }

    Assembly assembly;
    Vector3D origin;
    long count;
    long completed;
    ProgressLambda pl;
    Group visualizations;
    Group simVis;

    public MonteCarloSimulation(Assembly assembly, Vector3D origin) {
        this.assembly = assembly;
        this.origin = origin;
        this.simVis = new Group();
    }

    public void simulateNeutrons(long count, Group visualizations, ProgressLambda pl) {
        this.visualizations = visualizations;
        this.visualizations.getChildren().remove(simVis);
        this.visualizations.getChildren().add(simVis);
        this.count = count;
        this.completed = 0;
        this.simVis.getChildren().clear();
        this.pl = pl;

        pl.reportProgress(0);

//        Vector3D v0 = new Vector3D(200, 100, 100);
//        Vector3D v1 = new Vector3D(-100, 100, 100);
//        Vector3D v2 = new Vector3D(-100, 100, -200);
//        Util.Graphics.drawLine(simVis, v0, v1, Color.GREEN);
//        Util.Graphics.drawLine(simVis, v1, v2, Color.GREEN);
//        Util.Graphics.drawLine(simVis, v2, v0, Color.GREEN);
//
//        Vector3D start = new Vector3D(0,25,0);
//        double t = Util.Math.rayTriangleIntersect(start, Vector3D.PLUS_J, v0, v1, v2);
//        if (t != -1) {
//            System.out.println("t="+t);
//            Util.Graphics.drawSphere(simVis, Util.Math.rayPoint(start, Vector3D.PLUS_J, t), 5, "blue");
//        }
//        t = Util.Math.rayTriangleIntersect(Vector3D.ZERO, Vector3D.PLUS_I.add(Vector3D.PLUS_J.scalarMultiply(0.5)), v0, v1, v2);
//        Util.Graphics.drawSphere(simVis, Vector3D.ZERO.add(Vector3D.PLUS_I.add(Vector3D.PLUS_J.scalarMultiply(0.5)).scalarMultiply(t)), 5, "yellow");
//        
//        t = Util.Math.rayTriangleIntersect(Vector3D.ZERO, Vector3D.PLUS_I.add(Vector3D.PLUS_K.scalarMultiply(-0.5)), v0, v1, v2);
//        Util.Graphics.drawSphere(simVis, Vector3D.ZERO.add(Vector3D.PLUS_I.add(Vector3D.PLUS_K.scalarMultiply(-0.5)).scalarMultiply(t)), 5, "red");
//
//
//        for (int i = 0; i < 10; i++) {
//            Event e = assembly.rayIntersect(new Vector3D(25,0,0),
//                    Vector3D.PLUS_I.add(new Vector3D(0, Util.Math.random.nextDouble() * 2 - 1, Util.Math.random.nextDouble() * 2 - 1)),
//                    simVis);
//            System.out.println("Event: " + e);
//            Util.Graphics.visualizeEvent(e, simVis);
//        }
        System.out.println("");
        System.out.println("");
        System.out.println("Running new MC simulation for " + count + " neutrons ...");

        // reset detectors
        assembly.resetDetectors();

        // and enviroment (will count escaped neutrons)
        Environment.getInstance().reset();

        ArrayList<Neutron> neutrons = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            Vector3D direction = Util.Math.randomDir();
            if (Math.abs(direction.getNorm() - 1.0) > 1E-8) {
                System.out.println("hah!");
            }
            Neutron n = new Neutron(this.origin, direction, Neutron.startingEnergyDD);
            neutrons.add(n);
        }

        Thread th = new Thread(() -> {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            if (!MonteCarloSimulation.parallel) {
                neutrons.stream().forEach(n -> simulateNeutron(n));
            } else {
                neutrons.parallelStream().forEach(n -> simulateNeutron(n));
            }
        });

        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
    }

    public void simulateNeutron(Neutron n) {
        this.assembly.evolveNeutronPath(n, this.simVis, true);
        long current;
        synchronized (this) {
            completed++;
            current = completed;
            if (current * 1000 / count % 10 == 0) {
                Platform.runLater(() -> {
                    this.pl.reportProgress((int) (current * 100 / count));
                });
            }
        }
    }

    public BarChart makeChart(String detector, String series) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Energy (eV)");

        if (detector != null) {
            Part p = Part.getByName(detector);
            if (p == null) {
                return null;
            }
            DecimalFormat f = new DecimalFormat("0.###E0");
            String e = f.format(p.getTotalDepositedEnergy() * 1e-4 ); 
            bc.setTitle("Part \"" + p.name + "\", total deposited energy: "
                    + e + " J");

            switch (series) {
                case "EntryCounts":
                    yAxis.setLabel("Count");
                    bc.getData().add(p.entryOverEnergy.makeSeries("Entry counts"));
                    break;
                case "Fluence":
                    yAxis.setLabel("Fluence (cm^-2)");
                    bc.getData().add(p.fluenceOverEnergy.makeSeries("Fluence"));
                    break;
                default:
                    break;
            }
        } else {
            bc.setTitle("Environment");
            yAxis.setLabel("Count");
            bc.getData().add(Environment.getInstance().counts.makeSeries("Escape counts"));
        }
        return bc;
    }

}
