package org.eastsideprep.javaneutrons.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Part {

    public Shape shape;
    public Material material;
    public String name;

    // universal detector functionality
    public EnergyHistogram entriesOverEnergy;
    public EnergyHistogram exitsOverEnergy;
    public Map<String,EnergyHistogram>  fluenceMap; 
    public EnergyHistogram scattersOverEnergyBefore;
    public EnergyHistogram scattersOverEnergyAfter;
    public EnergyHistogram capturesOverEnergy;
    public Histogram angles;
    private double volume = 0;
    private double totalDepositedEnergy = 0;
    private int totalEvents = 0;

    public Part(String name, Shape s, Object material) {
        if (s != null) {
            this.shape = new Shape(s);
        }
        this.name = name;
        if (this.shape != null) {
            this.shape.part = this;
        }
        if (material != null) {
            String mName = material.toString();
            this.material = Material.getRealMaterial(material);
            if (this.material == null) {
                throw new IllegalArgumentException("invalid material for part: " + mName);
            }
        }
        if (this.shape != null) {
            this.volume = this.shape.getVolume();
        }
        resetDetector();
    }

    public void resetDetectors() {
        resetDetector();
    }

    public final void resetDetector() {
        this.totalDepositedEnergy = 0;
        this.totalEvents = 0;
        this.exitsOverEnergy = new EnergyHistogram();
        this.entriesOverEnergy = new EnergyHistogram();
        
        EnergyHistogram neutronFluence = new EnergyHistogram();
        EnergyHistogram gammaFluence = new EnergyHistogram();
        this.fluenceMap = new HashMap<>();
        this.fluenceMap.put("neutron", neutronFluence);
        this.fluenceMap.put("gamma", gammaFluence);
        
        this.scattersOverEnergyBefore = new EnergyHistogram();
        this.capturesOverEnergy = new EnergyHistogram();
        this.scattersOverEnergyAfter = new EnergyHistogram();
        this.angles = new Histogram(-1, 1, 100, false);
    }

    public static ArrayList<Part> NewPartsFromShapeList(String name, List<Shape> shapes, Material material) {
        ArrayList<Part> parts = new ArrayList<>();
        int i = 0;
        for (Shape s : shapes) {
            Part p = new Part(name + "." + (s.name != null ? s.name : String.format("%03d", i)), s, material);
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
    Event rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut, LinkedTransferQueue<Node> g) {
        int[] face = new int[1];
        double t = shape.rayIntersect(rayOrigin, rayDirection, goingOut, face, g);
        // not found?
        if (t == -1) {
            return null;
        }
        // construct appropriate event
        return new Event(Util.Math.rayPoint(rayOrigin, rayDirection, t),
                goingOut ? Event.Code.Exit : Event.Code.Entry,
                t,
                face[0]);
    }

    //
    // evolveNeutronPathNoVacuum
    // 
    // follows the neutron around from entry to exit or absorption
    // outermost will be ignored, this is not an assembly
    // 
    Event evolveParticlePath(Particle p, LinkedTransferQueue<Node> visualizations, boolean outermost, Grid grid) {
        double t;
        Event exitEvent;
        Event interactionEvent;
        Event event;
        double epsilon = 1e-10; // 10^-12m (in cm) 

        // entry into part - advance neutron ever so slightly
        // so that when something else happens, we will be firmly inside
        p.setPosition(visualizations, Util.Math.rayPoint(p.position, p.direction, epsilon));
        if (p.mcs.traceLevel >= 2) {
            System.out.println("Neutron " + p.hashCode() + " entry into part " + this.name);
            System.out.println(" Neutron energy in: " + String.format("%6.3e eV", p.energy / Util.Physics.eV));
        }
        this.processEntry(p);

        do {
            double currentEnergy = p.energy;
            // this next line will figure out where to scatter/absorb
            interactionEvent = p.nextPoint(material);

            if (grid != null) {
                exitEvent = grid.rayIntersect(p.position, p.direction, true, p.mcs.traceLevel >= 1 ? visualizations : null, interactionEvent.t);
                // DEBUG
//                if (exitEvent == null) {
//                    // find it the slow way
//                    exitEvent = this.rayIntersect(n.position, n.direction, false, visualizations);
//                    if (exitEvent != null) {
//                        System.out.println("oh oh.");
//                    }
//                }
            } else {
                exitEvent = this.rayIntersect(p.position, p.direction, true, visualizations);
            }

            if (exitEvent == null) {
                //throw new IllegalArgumentException();
                exitEvent = new Event(p.position.add(p.direction.scalarMultiply(10)), Event.Code.EmergencyExit, 10, 0);
                Util.Graphics.visualizeEvent(exitEvent, p.direction, visualizations);
                if (p.mcs.traceLevel >= 2) {
                    p.dumpEvents("--no way out of part, emergency exit, dumping events" + this.name);
                }
                event = exitEvent;
            } else {
                if (exitEvent.t > interactionEvent.t) {
                    // scattering / absorption did really happen, process it
                    event = interactionEvent;
                    Util.Graphics.visualizeEvent(event, visualizations);
                    p.setPosition(visualizations, event.position);
                    this.processEvent(event);
                } else {
                    // we exit the part before stuff happens
                    event = exitEvent;
                    p.setPosition(visualizations, event.position);
                    Util.Graphics.visualizeEvent(event, p.direction, visualizations);
                    this.processExit(p);
                    event.particle = p;
                    event.exitMaterial = this.shape.getContactMaterial(event.face);
                }
                // call for Detector parts to record
                this.material.processEvent(event, false);
                this.processPathLength(p, event.t, currentEnergy);
            }

            // also record event for the individual neutron
            if (!p.record(event)) {
                // too many events, get out
                return event;
            }
        } while (event.code != Event.Code.Exit && event.code != Event.Code.Capture);
        if (event.code == Event.Code.Capture) {
            if (p.mcs.traceLevel >= 2) {
                System.out.println("Neutron " + p.hashCode() + " captured in part " + this.name);
                System.out.println(" Neutron energy final: " + String.format("%6.3e eV", p.energy / Util.Physics.eV));
            }
        } else {
            // advance the neutron a bit to the outside
            p.setPosition(visualizations, Util.Math.rayPoint(p.position, p.direction, epsilon));
            if (p.mcs.traceLevel >= 2) {
                System.out.println("Neutron " + p.hashCode() + " exit from part " + this.name);
                System.out.println(" Neutron energy out: " + String.format("%6.3e eV", p.energy / Util.Physics.eV));
            }
        }

        return event;
    }

    //
    // detector functionality
    //
    void processPathLength(Particle particle, double length, double energy) {
        this.fluenceMap.get(particle.type).record(length / volume, energy);
    }

    void processEntry(Particle  p) {
        this.entriesOverEnergy.record(1, p.energy);
        p.entryEnergy = p.energy;

    }

    public void processEvent(Event event) {
        // record stats for part
        if (event.code == Event.Code.Scatter) {
            this.scattersOverEnergyBefore.record(1, event.particle.energy);
        } else {
            this.capturesOverEnergy.record(1, event.particle.energy);
        }

        // let the neutron do its thing
        event.particle.processEvent(event);

        // record more stats for part
        if (event.code == Event.Code.Scatter || event.code == Event.Code.Capture) {
            synchronized (this) {
                this.totalEvents++;
            }
        }

        if (event.code == Event.Code.Scatter) {
            this.scattersOverEnergyAfter.record(1, event.particle.energy);
            this.angles.record(1, event.cos_theta);
        }
    }

    synchronized void processExit(Particle p) {
        this.exitsOverEnergy.record(1, p.energy);
        this.totalDepositedEnergy += (p.entryEnergy - p.energy);
    }

    public double getTotalDepositedEnergy() {
        return this.totalDepositedEnergy;
    }

    public double getTotalFluence(String kind) {
        double fluence = 0;
        synchronized(this) {
            // todo: integrate appropriate histogram
        }
        return fluence;
    }

    public double getTotalPath(String kind) {
        return this.getTotalFluence(kind) * this.volume;
    }

    public int getTotalEvents() {
        return this.totalEvents;
    }

    public ObservableList<Transform> getTransforms() {
        //System.out.println("part "+this+" Tx "+this.shape.getTransforms());
        return this.shape.getTransforms();
    }

    public void addTransform(Transform t) {
        this.shape.getTransforms().add(t);
    }

    public void addTransform(int i, Transform t) {
        this.shape.getTransforms().add(i, t);
    }

    public void setColor(String color) {
        this.shape.setColor(color);
    }

    public Translate settleAgainst(Part other, Vector3D f) {

        return shape.settleAgainst(other.shape, f);
    }

    // what is the distance from our vertices to the other thing, 
    // and vice-versa?
    public double distance(Part other, Vector3D direction) {
        return shape.distance(other.shape, direction);
    }

}
