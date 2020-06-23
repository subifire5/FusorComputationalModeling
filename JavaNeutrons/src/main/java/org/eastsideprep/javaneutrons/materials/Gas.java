/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Element;
import org.eastsideprep.javaneutrons.assemblies.Material;

/**
 *
 * @author gmein
 */
//
// air is the material for NegativeSpace - everything around the parts 
// of an Assembly
public abstract class Gas extends Material {

    private static Gas instance;
    private double pressure;

    // pressure is in kPa

    Gas(String name, double pressure) {
        super(name);
        this.pressure = pressure;
    }
    
    // use this for a single-element gas
    Gas(String name, Element element, double pressure, double massDensitySTP){
        super(name);
        this.pressure = pressure;
        
        this.addComponent(element, 1);
        this.calculateAtomicDensities(massDensitySTP * pressure);
    }

}
