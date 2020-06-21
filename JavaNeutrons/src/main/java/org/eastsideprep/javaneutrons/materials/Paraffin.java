/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.Material;

//
// Paraffin
// Cn H(2n+2)
// one major component is hentriacontane n=31: C31 H64
// source: https://en.wikipedia.org/wiki/Paraffin_wax
// density 781 kg/m^3
// source: Google answer "hentriacontane density"
//
public class Paraffin extends Material {

    static Paraffin instance;
    
    Paraffin() {
        super("Paraffin");
        this.addComponent(Carbon.getInstance(), 31);
        this.addComponent(Hydrogen.getInstance(), 64);
        this.calculateAtomicDensities(781);
    }

    Paraffin(String name) {
        super(name);
    }
    
    // we only need one of these objects
    public static Paraffin getInstance() {
        if (instance == null) {
            Paraffin.instance = new Paraffin();
        }
        return instance;
    }

}
