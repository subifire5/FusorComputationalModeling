/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.Material;

/**
 *
 * @author gmein
 */
//
// air is the material for NegativeSpace - everything around the parts 
// of an Assembly
public class Air extends Material {

    static Air instance;
    double pressure;
    
    // pressure is in kPa
    Air(double pressure) {
        super("Air");
        this.pressure = pressure;
        // todo: add real components of air
        // instead, we add co2 
        
        double massDensitySTP = 1.960;
        
        this.addComponent(Carbon.getInstance(), 1);
        this.addComponent(Oxygen.getInstance(), 2);
        this.calculateAtomicDensities(massDensitySTP*pressure/100);
    }

    Air(String name, double pressure) {
        super(name);
        this.pressure = pressure;
    }
    
    // we only need one of these objects
    public static Air getInstance() {
        if (instance == null) {
            Air.instance = new Air(100);
        }
        return instance;
    }

}
