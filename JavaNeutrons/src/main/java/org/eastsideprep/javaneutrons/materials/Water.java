/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Material;

//
// Water (H2O)
//
public class Water extends Material {

    static Water instance;

    Water() {
        super("Water");
        this.addComponent(Hydrogen.getInstance(), 2);
        this.addComponent(Oxygen.getInstance(), 1);
        this.calculateAtomicDensities(997);
    }

    Water(String name) {
        super(name);
    }

    // we only need one of these objects
    public static synchronized Water getInstance() {
        if (instance == null) {
            Water.instance = new Water();
        }
        return instance;
    }

}
