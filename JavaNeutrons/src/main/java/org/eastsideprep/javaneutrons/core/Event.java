/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import org.eastsideprep.javaneutrons.core.Neutron;
import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.eastsideprep.javaneutrons.core.Element;
import org.eastsideprep.javaneutrons.core.Material;
import org.eastsideprep.javaneutrons.core.Part;

/**
 *
 * @author gunnar
 */
public class Event {

    public enum Code {
        Entry, Exit, Scatter, Capture, Gone, EmergencyDirectionChange, EmergencyExit
    };

    public Code code; // what kind of interesting thing happened here
    public Vector3D position;
    public Neutron neutron;

    // additional info - presence depends on event
    public Element element;
    public double energyOut;
    public double t; // how far along was this on the vector we took to get here
    public Part part;
    public int face;
    public Material exitMaterial;

    public Event(double x, double y, double z, Event.Code c) {
        this.position = new Vector3D(x, y, z);
        this.code = c;
    }

    public Event(double x, double y, double z, Event.Code c, double t) {
        this.position = new Vector3D(x, y, z);
        this.code = c;
        this.t = t;
    }

    public Event(Vector3D position, Event.Code c) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
    }

    public Event(Vector3D position, Event.Code c, double t, int face) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
        this.face = face;
    }

    public Event(Vector3D position, Event.Code c, double t, Element e, Neutron n) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
        this.element = e;
        this.neutron = n;
    }

    public Event(Vector3D position, Part p, double t) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.part = p;
        this.code = Event.Code.Entry;
        this.t = t;
    }

    @Override
    public String toString() {
        return "Event: " + this.hashCode() + ": " + this.code + ": "+this.position;
    }
}
