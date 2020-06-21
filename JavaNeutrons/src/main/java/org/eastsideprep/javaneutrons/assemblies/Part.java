/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.assemblies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.shape.DrawMode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.core.Event;
import org.eastsideprep.javaneutrons.core.LogHistogram;
import org.eastsideprep.javaneutrons.core.Neutron;
import org.eastsideprep.javaneutrons.core.Util;
import org.eastsideprep.javaneutrons.shapes.Shape;

/**
 *
 * @author gunnar
 */
public class Part {

    static HashMap<String, Part> namedParts = new HashMap<>();
    Shape shape;
    Material material;
    public String name;

    // universal detector functionality
    public LogHistogram entryOverEnergy;
    public LogHistogram fluenceOverEnergy;
    private double volume = 0;
    private double currentEntryEnergy = 0;
    private double totalDepositedEnergy = 0;

    public Part(String name, Shape s, Object material) {
        this.shape = s;
        this.name = name;
        if (this.shape != null) {
            this.shape.part = this;
            namedParts.put(name, this);
        }
        this.material = Material.getRealMaterial(material);
        if (this.shape != null) {
            this.volume = this.shape.getVolume();
        }
        resetDetector();
    }

    public static Part getByName(String name) {
        return namedParts.get(name);
    }

    public void resetDetectors() {
        resetDetector();
    }

    public final void resetDetector() {
        this.currentEntryEnergy = 0;
        this.totalDepositedEnergy = 0;
        this.entryOverEnergy = new LogHistogram(-5, 10, 45);
        this.fluenceOverEnergy = new LogHistogram(-5, 10, 45);
    }

    public static ArrayList<Part> NewPartsFromShapeList(String name, List<Shape> shapes, Material material) {
        ArrayList<Part> parts = new ArrayList<>();
        int i = 0;
        for (Shape s : shapes) {
            Part p = new Part(name + "." + i, s, material);
            p.shape.setDrawMode(DrawMode.LINE);
            p.shape.setOpacity(0.5);
            p.shape.setColor("blue");
            parts.add(p);
            i++;
        }
        return parts;
    }

    //
    // rayIntersect
    //
    // intersects a ray with all triangles of this parts, returns the t-param of the closest
    // goingOut determines whether we are entering or leaving the part (true=test back faces)
    //
    Event rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut, Group g) {
        double t = shape.rayIntersect(rayOrigin, rayDirection, goingOut, g);
        // not found?
        if (t == -1) {
            return null;
        }
        // construct appropriate event
        return new Event(Util.Math.rayPoint(rayOrigin, rayDirection, t), goingOut ? Event.Code.Exit : Event.Code.Entry, t);
    }

    //
    // evolveNeutronPathNoVacuum
    // 
    // follows the neutron around from entry to exit or absorption
    // outermost will be ignored, this is not an assembly
    // 
    Event evolveNeutronPath(Neutron n, Group visualizations, boolean outermost) {
        double t;
        Event exitEvent;
        Event interactionEvent;
        Event event;
        double epsilon = 1e-7; // 1 nm (in cm) 

        // entry into part - tally, advance neutron ever so slightly
        // so that when something else happens, we will be firmly inside
        n.setPosition(Util.Math.rayPoint(n.position, n.direction, epsilon));

        this.processEntryEnergy(n.energy);

        do {
            double currentEnergy = n.energy;

//            // kind of awful, retry loop to get out of part
//            int i = 10;
//            do {
            exitEvent = this.rayIntersect(n.position, n.direction, true, visualizations);
//                n.randomizeDirection();
//                n.record (new Event(n.position, Event.Code.EmergencyDirectionChange));
//                i--;
//            } while (exitEvent == null && i > 0);
//            // might not succeed

            if (exitEvent == null) {
                System.out.println("");
                System.out.println("--no way out of part, emergency exit, dumping events" + this.name);
                //throw new IllegalArgumentException();
                exitEvent = new Event(n.position.add(n.direction.scalarMultiply(10)), Event.Code.EmergencyExit, 10);
                n.record(exitEvent);
                n.dumpEvents();
                Util.Graphics.visualizeEvent(exitEvent, n.direction, visualizations);
                System.out.println("--end dump");
                return exitEvent;
            }

            // this next line will figure out where to scatter/absorb
            interactionEvent = material.nextPoint(n);
            if (exitEvent.t > interactionEvent.t) {
                // scattering / absorption did really happen, process it
                event = interactionEvent;
                Util.Graphics.visualizeEvent(event, visualizations);
                n.processEvent(event);
            } else {
                event = exitEvent;
                n.setPosition(event.position);
                Util.Graphics.visualizeEvent(event, n.direction, visualizations);
                this.processExitEnergy(n.energy);
            }
            // call for Detector parts to record
            this.processPathLength(event.t, currentEnergy);

            // also record event for the individual neutron
            n.record(event);
        } while (event.code != Event.Code.Exit && event.code != Event.Code.Capture);

        return event;
    }

    //
    // detector functionality
    //
    void processPathLength(double length, double energy) {
        if (name.equals("Body")) {
            System.out.println("Entry into detector path length log " + this.fluenceOverEnergy.hashCode());
        }
        this.fluenceOverEnergy.record(length / volume, energy/Util.Physics.eV);
    }

    void processEntryEnergy(double e) {
        if (name.equals("Body")) {
            System.out.println("Entry into detector entry energy log " + this.entryOverEnergy.hashCode());
            this.entryOverEnergy.record(1, e/Util.Physics.eV);
            this.currentEntryEnergy = e;
        }
    }

    void processExitEnergy(double e
    ) {
        this.totalDepositedEnergy += (e - this.currentEntryEnergy);
    }

    public double getTotalDepositedEnergy() {
        return this.totalDepositedEnergy;
    }

}
