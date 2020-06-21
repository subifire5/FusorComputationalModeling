/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.*;

/**
 *
 * @author gunnar
 */
public class Event {

    public enum Code {
        Entry, Exit, Scatter, Capture, Gone, EmergencyExit
    };

    Code code; // what kind of interesting thing happened here
    Vector3D position;
    Neutron neutron;

    // additional info - presence depends on event
    Element element;
    double energyOut;
    double t; // how far along was this on the vector we took to get here
    Part part;

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

    public Event(Vector3D position, Event.Code c, double t) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
    }

    public Event(Vector3D position, Event.Code c, double t, Element e) {
        this.position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
        this.element = e;
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
