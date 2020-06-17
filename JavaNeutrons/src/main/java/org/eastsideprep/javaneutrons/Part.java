/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class Part {

    Shape shape;
    Material material;

    public Part(Shape s, Material m) {
        this.shape = s;
        this.material = m;
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
                if (event.code == Event.Code.Scatter) {
                    n.elasticScatter(event.element);
                }
                // todo: add event to neutron history
                t = t2;
            } else {
                // process exit event
                event = new Event(n.position.add(n.direction.scalarMultiply(t1)), Event.Code.Exit);
                t = t1;
            }
            processPathLength(t);
        } while (event.code != Event.Code.Exit && event.code != Event.Code.Absorb);

        return event;
    }

    void processPathLength(double t) {
        // this is an empty method to be overridden by Detector class
    }
}
