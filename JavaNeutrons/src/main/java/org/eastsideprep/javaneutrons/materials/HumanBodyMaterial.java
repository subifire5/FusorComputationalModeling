/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Material;

//
// Human tissue
// source:https://en.wikipedia.org/wiki/Composition_of_the_human_body#:~:text=Almost%2099%25%20of%20the%20mass,11%20are%20necessary%20for%20life.
//
public class HumanBodyMaterial extends Material {

    static HumanBodyMaterial instance;

    HumanBodyMaterial() {
        super("HumanBodyMaterial");
        this.addComponent(Hydrogen.getInstance(), 0.62);
        this.addComponent(Oxygen.getInstance(), 0.24);
        this.addComponent(Carbon.getInstance(), 0.12);
        this.addComponent(Nitrogen.getInstance(), 0.11);
        this.calculateAtomicDensities(1000);
    }

    HumanBodyMaterial(String name) {
        super(name);
    }

    // we only need one of these objects
    public static synchronized HumanBodyMaterial getInstance() {
        if (instance == null) {
            HumanBodyMaterial.instance = new HumanBodyMaterial();
        }
        return instance;
    }

}
