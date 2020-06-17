/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gmein
 */
public class PointOfInterest extends Vector3D {
    public enum Code {Entry, Exit, Scatter, Absorb};
    
    Code code; // what kind of interesting thing happened here
    double t; // how far along was this on the vector we took to get here
    Element element; // what element was involved
    
    public PointOfInterest (double x, double y, double z, PointOfInterest.Code c) {
        super(x, y, z);
        this.code = c;
    }
    
    public PointOfInterest (double x, double y, double z, PointOfInterest.Code c, double t) {
        super(x, y, z);
        this.code = c;
        this.t = t;
    }
    
   public PointOfInterest (Vector3D position, PointOfInterest.Code c) {
        super(position.getX(), position.getY(), position.getZ());
        this.code = c;
    }
   
   public PointOfInterest (Vector3D position, PointOfInterest.Code c, double t, Element e) {
        super(position.getX(), position.getY(), position.getZ());
        this.code = c;
        this.t = t;
        this.element = e;
    }
}
