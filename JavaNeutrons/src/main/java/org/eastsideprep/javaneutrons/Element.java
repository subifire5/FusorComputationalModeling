/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

/**
 *
 * @author gunnar
 */
public class Element extends Material {

    double mass; // g
    int atomicNumber;
    int neutrons;

    Element(String name, double mass, int atomicNumber, int neutrons) {
        super(name);
        this.mass = mass;
        this.atomicNumber = atomicNumber;
        this.neutrons = neutrons;
        super.addComponent(this, 1.0);
    }

    public double getScatterCrossSection(double energy) {
        // todo: this is Taras' job
        return 0;
    }

    public double getAbsorptionCrossSection(double energy) {
        // todo: this is Taras' job
        return 0;
    }
}
