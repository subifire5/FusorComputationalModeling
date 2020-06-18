/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Detector extends Part {
    LogHistogram hist;
    double pathLength;

    public Detector(Vector3D location, float size) {
        // needs shape (cube?)
        // and material (some simple one-element stuff?)
        super("Detector", new Cube(size), null);
        hist = new LogHistogram(-5,10,50);
    }

    @Override
    void processPathLength(double t) {
        // todo: Have Dr. Whitmer explain this approach again
        pathLength += t;
    }
    
    void reset() {
        pathLength = 0;
    }
    
    void tally() {
        hist.record(pathLength);
        pathLength = 0;
    }
}
