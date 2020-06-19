/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.Group;
import javafx.scene.Node;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import static org.eastsideprep.javaneutrons.Shape.rayTriangleIntersect;

/**
 *
 * @author gunnar
 */
public class Assembly extends Group {
    // todo: acceleration structure
    
    ArrayList<Detector> detectors = new ArrayList<>();

    Assembly(String name) {
    }

    public Event evolveNeutronPathNoVacuum(Neutron n) {
        Air air = Air.getInstance();
        Event partEvent;
        Event interactionEvent;
        Event event;
        double t;

        do {
            // find the closest part we intersect with
            partEvent = this.rayIntersect(n.position, n.direction);
            // find possible interactions along the way
            interactionEvent = air.nextPoint(n);

            // did we not find a part, or is it further than an air event?
            if (partEvent == null || partEvent.t > interactionEvent.t) {
                // scattering / absorption in air did really happen, process it
                n.setPosition(interactionEvent.position);
                n.processEvent(interactionEvent);
                event = interactionEvent;
            } else {
                // no interaction, we will just enter a new part
                Part p = partEvent.part;
                event = partEvent;
            }
            // if neutron goes far enough from the origin, call it gone
            if (n.position.getNorm() > Environment.limit) {
                Environment.processEnergy(n.energy);
                event.code = Event.Code.Gone;
            }
        } while (event.code != Event.Code.Absorb && event.code != Event.Code.Gone);
        return event;
    }

    //
    // rayIntersect
    //
    // intersect with all parts in assembly, return event with 
    // part or null
    //
    Event rayIntersect(Vector3D rayOrigin, Vector3D rayDirection) {
        double tmin = 0;
        Part closestPart = null;

        for (Node node : this.getChildren()) {
            if (node instanceof Shape) {
                // link back to the Part that contains it
                Part p = ((Shape) node).part;
                // intersect with that part, false means "going in"
                double t = p.rayIntersect(rayOrigin, rayDirection, false);
                // t !=0 means we found a triangle
                if (t != 0) {
                    if (tmin == 0 || t < tmin) {
                        tmin = t;
                        closestPart = p;
                    }
                }
            }
        }

        return (tmin == 0) ? null : new Event(rayOrigin.add(rayDirection.scalarMultiply(tmin)), closestPart);
    }

    public void add(Part part) {
        this.getChildren().add(part.shape);
        if (part instanceof Detector) {
            this.detectors.add((Detector) part);
        }
    }

    public void addAll(Part... parts) {
        for (Part p : parts) {
            this.getChildren().add(p.shape);
        }
    }
}
