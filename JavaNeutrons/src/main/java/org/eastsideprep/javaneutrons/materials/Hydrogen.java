/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.Element;
import org.eastsideprep.javaneutrons.Util;

//
// Molecular hydrogen in molecular form
//
public class Hydrogen extends Element {

    private static Hydrogen instance;
    
    Hydrogen() {
        super("Hydrogen", 1, 0);
    }

    @Override
    public double getScatterCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10*Util.Physics.eV){
            return 3e-24;
        } else{
            return 4e-24;
        }
    }

    @Override
    public double getCaptureCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10*Util.Physics.eV){
            return 0.000007*1e-24;
        } else{
            return 0.0003*1e-24;
        }
    }
    
        // we only need one of these objects
    public static Hydrogen getInstance() {
        if (instance == null) {
            Hydrogen.instance = new Hydrogen();
        }
        return instance;
    }

}
