/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.materials.Air;

/**
 *
 * @author gunnar
 */
public class Assembly extends Group {
    // todo: acceleration structure

    ArrayList<Detector> detectors = new ArrayList<>();

    Assembly(String name) {
    }

    public Event evolveNeutronPathNoVacuum(Neutron n, Group visualizations) {
        Air air = Air.getInstance();
        Event partEvent;
        Event interactionEvent;
        Event event;
        double t;

        do {
            // find the closest part we intersect with
            partEvent = this.rayIntersect(n.position, n.direction, visualizations);
            // find possible interactions along the way
            interactionEvent = air.nextPoint(n);

            // did we not find a part, or is it further than an air event?
            if (partEvent == null || partEvent.t > interactionEvent.t) {
                event = interactionEvent;
                if (event.position.getNorm() <= Environment.limit) {
                    // scattering / absorption in air did really happen, process it
                    n.setPosition(event.position);
                    n.processEvent(event);
                    Util.Graphics.visualizeEvent(event, visualizations);
                }
            } else {
                // no interaction, we will just enter a new part
                Util.Graphics.visualizeEvent(partEvent, visualizations);
                Part p = partEvent.part;
                n.setPosition(partEvent.position);
                //System.out.println("Entering part " + p.name);
                event = p.evolveNeutronPath(n, visualizations);
            }
            // if things happened far enough from the origin, call it gone
            if (event.position.getNorm() > Environment.limit) {
                Environment.processEnergy(n.energy);
                event = new Event(n.position.add(n.direction.scalarMultiply(10)), Event.Code.Gone, 10);
            }
            //visualizeEvent(event, visualizations);
        } while (event.code != Event.Code.Capture && event.code != Event.Code.Gone && event.code != Event.Code.EmergencyExit);
        return event;
    }

    //
    // rayIntersect
    //
    // intersect with all parts in assembly, return event with 
    // part or null
    //
    Event rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, Group g) {
        double tmin = -1;
        Part closestPart = null;

        for (Node node : this.getChildren()) {
            if (node instanceof Shape) {
                // link back to the Part that contains it
                Part p = ((Shape) node).part;
                // intersect with that part, false means "going in"
                Event entryEvent = p.rayIntersect(rayOrigin, rayDirection, false, g);
                // event != null means we found a triangle
                if (entryEvent != null) {
                    if (tmin == -1 || entryEvent.t < tmin) {
                        tmin = entryEvent.t;
                        closestPart = p;
                    }
                }
            }
        }

        return (tmin == -1) ? null : new Event(rayOrigin.add(rayDirection.scalarMultiply(tmin)), closestPart, tmin);
    }

    public void add(Part part) {
        Platform.runLater(() -> this.getChildren().add(part.shape));
        if (part instanceof Detector) {
            this.detectors.add((Detector) part);
        }
    }

    public void addAll(Part... parts) {
        for (Part p : parts) {
            this.add(p);
        }
    }
}
