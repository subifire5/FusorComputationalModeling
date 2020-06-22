/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.assemblies;

import java.net.URL;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.core.Environment;
import org.eastsideprep.javaneutrons.core.Event;
import org.eastsideprep.javaneutrons.core.Neutron;
import org.eastsideprep.javaneutrons.core.Util;
import org.eastsideprep.javaneutrons.materials.Air;
import org.eastsideprep.javaneutrons.shapes.AssemblyGroup;
import org.eastsideprep.javaneutrons.shapes.Shape;

/**
 *
 * @author gunnar
 */
public class Assembly extends Part {
    // todo: acceleration structure

    private AssemblyGroup g;

    public Assembly(String name) {
        super(name, null, null);
        this.g = new AssemblyGroup(this);
    }

    // use this constructor to construct an assembly from an OBJ file
    public Assembly(String name, URL url, Object material) {
        super(name, null, material);

        material = Material.getRealMaterial(material);

        this.g = new AssemblyGroup(this);

        ArrayList<Shape> shapes = Shape.loadOBJ(url);
        this.addAll(Part.NewPartsFromShapeList(name, shapes, (Material) material));
    }

    public Group getGroup() {
        return g;
    }

    @Override
    public void resetDetectors() {
        for (Node n : g.getChildren()) {
            if (n instanceof AssemblyGroup) {
                ((AssemblyGroup) n).assembly.resetDetectors();
            } else if (n instanceof Shape) {
                ((Shape) n).part.resetDetectors();
            }
        }
    }

    @Override
    public Event evolveNeutronPath(Neutron n, Group visualizations, boolean outermost) {
        return this.evolveNeutronPathNoVacuum(n, visualizations, outermost);
    }

    public Event evolveNeutronPathNoVacuum(Neutron n, Group visualizations, boolean outermost) {
        Air air = Air.getInstance();
        Event partEvent;
        Event interactionEvent;
        Event event;
        double t;

        do {
            // find the closest part we intersect with
            partEvent = this.rayIntersect(n.position, n.direction, false, visualizations);
            if (partEvent == null && !outermost) {
                // we found nothing and we are not in the outermost assembly,
                // return to the containing assembly
                return new Event(n.position, Event.Code.Exit);
            }
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
                Util.Graphics.visualizeEvent(partEvent, n.direction, visualizations);
                Part p = partEvent.part;
                n.setPosition(partEvent.position);
                n.record(partEvent);
                //System.out.println("Entering part " + p.name);
                event = p.evolveNeutronPath(n, visualizations, false);
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
    // we need to ignore "going out" here, as we are not inside a simplepart
    //
    @Override
    Event rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut, Group vis) {
        double tmin = -1;
        Part closestPart = null;
        Part p = null;

        for (Node node : this.g.getChildren()) {
            if (node instanceof Shape) {
                // link back to the Part that contains it
                p = ((Shape) node).part;
            } else if (node instanceof AssemblyGroup) {
                // link back to the Assembly that contains it
                p = ((AssemblyGroup) node).assembly;
            }
            if (p != null) {
                // intersect with that part, false means "going in"
                Event entryEvent = p.rayIntersect(rayOrigin, rayDirection, false, vis);
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
        if (part instanceof Assembly) {
            Assembly a = (Assembly) part;
            g.getChildren().add(a.getGroup());
        } else {
            g.getChildren().add(part.shape);
        }
//        if (part.shape != null) {
//            System.out.println("Added part " + part.name + ", extent: " + part.shape.getExtent());
//        } 
    }

    public void addAll(Part... parts) {
        for (Part p : parts) {
            this.add(p);
        }
    }

    public void addAll(ArrayList<Part> parts) {
        for (Part p : parts) {
            this.add(p);
        }
    }

    public ObservableList<Transform> getTransforms() {
        return g.getTransforms();
    }
}
