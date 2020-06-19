/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.HashMap;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class Part {

    static HashMap<String, Part> namedParts = new HashMap<>();
    Shape shape;
    Material material;

    public Part(String name, Shape s, Material m) {
        this.shape = s;
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
    double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut) {
        return shape.rayIntersect(rayOrigin, rayDirection, goingOut);
    }

    //
    // evolveNeutronPathNoVacuum
    // 
    // follows the neutron around from entry to exit or absorption
    // 
    Event evolveNeutronPath(Neutron n) {
        double t, t1, t2;
        Event event = null;

        this.processEntryEnergy(n.energy);

        do {
            double currentEnergy = n.energy;
            t1 = this.rayIntersect(n.position, n.direction, true);
            assert (t1 != 0); // we are inside a part, there should be an exit point

            // this next line will figure out where to scatter/absorb
            event = material.nextPoint(n);
            t2 = event.t;

            if (t1 > t2) {
                // scattering / absorption did really happen, process it
                n.processEvent(event);
                event.energyOut = n.energy;
                t = t2;
            } else {
                // process exit event
                event = new Event(n.position.add(n.direction.scalarMultiply(t1)), Event.Code.Exit);
                this.processExitEnergy(n.energy);
                t = t1;
            }
            // call for Detector parts to record
            this.processPathLength(t, currentEnergy);

            // also record event for the individual neutron
            n.record(event);
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
