/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

/**
 *
 * @author gmein
 */
//
// air is the material for NegativeSpace - everything around the parts 
// of an Assembly
public class Air extends Material {

    static Air instance;
    // todo: nitrogen, oxygen, co2....

    // we only need one of these objects
    public static Air getInstance() {
        if (instance == null) {
            Air.instance = new Air();
        }
        return instance;
    }

}
