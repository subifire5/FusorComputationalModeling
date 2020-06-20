/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.HashMap;
import javafx.scene.Group;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class Part {

    static HashMap<String, Part> namedParts = new HashMap<>();
    Shape shape;
    Material material;
    String name;

    public Part(String name, Shape s, Material m) {
        this.shape = s;
        this.name = name;
        if (this.shape != null) {
            this.shape.part = this;
        }
        this.material = m;
        namedParts.put(name, this);
    }

    public static Part getByName(String name) {
        return namedParts.get(name);
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
        if (t == -1 ) {
            return null;
        }
        // construct appropriate event
        return new Event(rayOrigin.add(rayDirection.scalarMultiply(t)), goingOut?Event.Code.Exit:Event.Code.Entry, t);
    }

    //
    // evolveNeutronPathNoVacuum
    // 
    // follows the neutron around from entry to exit or absorption
    // 
    Event evolveNeutronPath(Neutron n, Group visualizations) {
        double t, t1, t2;
        Event exitEvent;
        Event interactionEvent;
        Event event;

        this.processEntryEnergy(n.energy);

        do {
            double currentEnergy = n.energy;
            exitEvent = this.rayIntersect(n.position, n.direction, true, visualizations);
            if (exitEvent == null) {
                System.out.println("no way out of part!");
                //throw new IllegalArgumentException();
                exitEvent=new Event(n.position.add(n.direction.scalarMultiply(100)), Event.Code.EmergencyExit, 100);
            }

            // this next line will figure out where to scatter/absorb
            interactionEvent = material.nextPoint(n);
            if (exitEvent.t > interactionEvent.t) {
                // scattering / absorption did really happen, process it
                event = interactionEvent;
                n.processEvent(event);
            } else {
                event = exitEvent;
                n.setPosition(event.position);
                this.processExitEnergy(n.energy);
            }
            // call for Detector parts to record
            this.processPathLength(event.t, currentEnergy);

            // also record event for the individual neutron
            n.record(event);
            Util.Graphics.visualizeEvent(event, visualizations);
        } while (event.code != Event.Code.Exit && event.code != Event.Code.Absorb);

        return event;
    }

    void processPathLength(double length, double energy) {
        // this is an empty method to be overridden by Detector class
    }

    void processEntryEnergy(double e) {
        // this is an empty method to be overridden by Detector class
    }

    void processExitEnergy(double e) {
        // this is an empty method to be overridden by Detector class
    }
}
