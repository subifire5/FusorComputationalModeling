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
        Entry, Exit, Scatter, Absorb
    };

    Code code; // what kind of interesting thing happened here
    Neutron neutron;
    Element element;
    Vector3D position;
    double t; // how far along was this on the vector we took to get here

    public Event(double x, double y, double z, Event.Code c) {
        position = new Vector3D(x, y, z);
        this.code = c;
    }

    public Event(double x, double y, double z, Event.Code c, double t) {
        position = new Vector3D(x, y, z);
        this.code = c;
        this.t = t;
    }

    public Event(Vector3D position, Event.Code c) {
        position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
    }

    public Event(Vector3D position, Event.Code c, double t, Neutron n, Element e) {
        position = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
        this.element = e;
    }
}
