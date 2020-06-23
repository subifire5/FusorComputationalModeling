/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Material;

/**
 *
 * @author gmein
 */
//
// air is the material for NegativeSpace - everything around the parts 
// of an Assembly
public class Air extends Gas {

    private static Air instance;
    private double pressure;

    // pressure is in kPa
    Air(double pressure) {
        super("Air", pressure);
        construct(pressure);
    }

    // use this for air at different pressure
    Air(String name, double pressure) {
        super(name, pressure);
        construct(pressure);
    }

    private void construct(double pressure) {
        this.pressure = pressure;

        // Google answer "density of air"
        double massDensitySTP = 1.225;

        this.addComponent(Nitrogen.getInstance(), 78.08);
        this.addComponent(Oxygen.getInstance(), 20.09);

        // that's 100 Pa for STP
        this.calculateAtomicDensities(massDensitySTP * pressure / 100);
    }

    // we only need one of these objects
    public static synchronized Air getInstance() {
        if (instance == null) {
            Air.instance = new Air(100);
        }
        return instance;
    }

}
