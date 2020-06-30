/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.core.Event;

public class HydrogenGas extends Gas {

    private static HydrogenGas instance;

    // use this for Air (or getInstance()
    // pressure is in kPa
    HydrogenGas(double pressure) {
        this("HydrogenGas", pressure);
    }

    // use this for air at different pressure
    // give it a different name, of course
    HydrogenGas(String name, double pressure) {
        super(name, pressure);
        
        double massDensitySTP = 0.084;
        massDensitySTP = 930.0*2.0/3.0;

        this.addComponent(Hydrogen.getInstance(), 1);

        // that's 100 Pa for STP
        this.calculateAtomicDensities(massDensitySTP * pressure / 100);
    }


    // we only need one of these objects
    public static synchronized HydrogenGas getInstance() {
        if (instance == null) {
            HydrogenGas.instance = new HydrogenGas(100);
        }
        return instance;
    }
    
    @Override
    public void processEvent(Event e){
//        if (e.code == Event.Code.Capture && e.neutron.energy >= 2*Util.Physics.eV){
//            System.out.println("hah!");
//        }
        super.processEvent(e);
    }

}
