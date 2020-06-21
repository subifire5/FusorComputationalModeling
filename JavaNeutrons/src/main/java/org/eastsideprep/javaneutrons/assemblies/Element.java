/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.assemblies;

import org.eastsideprep.javaneutrons.core.Neutron;
import org.eastsideprep.javaneutrons.core.Util;

/**
 *
 * @author gunnar
 */
public class Element extends Material {

    public double mass; // g
    int atomicNumber;
    int neutrons;

    public Element(String name, int atomicNumber, int neutrons) {
        super(name);
        this.atomicNumber = atomicNumber;
        this.neutrons = neutrons;
        this.mass = this.atomicNumber*Util.Physics.protonMass + 
                this.neutrons*Neutron.mass;
        super.addComponent(this, 1);
        // todo: what is an appropriate density for elements as materials?
        // mostly, this value will not be used as a component material
        // will have proportions of this element, and it own density
        super.calculateAtomicDensities(1);
    }

    public double getScatterCrossSection(double energy) {
        // todo: this is Taras' job
        return 0;
    }

    public double getCaptureCrossSection(double energy) {
        // todo: this is Taras' job
        return 0;
    }
}
