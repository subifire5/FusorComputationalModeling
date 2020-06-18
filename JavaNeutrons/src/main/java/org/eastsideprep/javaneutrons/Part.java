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
        this.material = m;
        namedParts.put(name, this);
    }
    
    public static Part getByName(String name) {
        return namedParts.get(name);
    }

    double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut) {
        return shape.rayIntersect(rayOrigin, rayDirection, goingOut);
    }

    Event evolveNeutronPath(Neutron n) {
        double t, t1, t2;
        Event event = null;

        do {
            t1 = rayIntersect(n.position, n.direction, true);
            assert (t1 != 0); // we are inside a material, unless it is NegativeSpace there should be an exit point

            // this next line will figure out where to scatter/absorb
            event = material.nextPoint(n); // todo: this needs to return a POI with the element
            t2 = event.t;

            if (t1 > t2) {
                // scattering / absorption did really happen, process it
                n.setPosition(event.position);
                if (event.code == Event.Code.Scatter) {
                    n.elasticScatter(event.element);
                } else {
                    // anything to do for absorption?
                }
                event.energyOut = n.energy;
                t = t2;
            } else {
                // process exit event
                event = new Event(n.position.add(n.direction.scalarMultiply(t1)), Event.Code.Exit);
                t = t1;
            }
            // call for Detector parts to record
            processPathLength(t);
            
            // also record event for the individual neutron
             n.record(event);
        } while (event.code != Event.Code.Exit && event.code != Event.Code.Absorb);

        return event;
    }

    void processPathLength(double t) {
        // this is an empty method to be overridden by Detector class
    }
}
