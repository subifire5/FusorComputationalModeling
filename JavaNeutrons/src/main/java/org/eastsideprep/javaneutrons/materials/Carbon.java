/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Element;
import org.eastsideprep.javaneutrons.core.Util;

/**
 *
 * @author gunnar
 */
public class Carbon extends Element {

    private static Carbon instance;

    Carbon() {
        super("12-Carbon", 6, 6);
    }

    @Override
    public double getScatterCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10 * Util.Physics.eV) {
            return 2e-24;
        } else {
            return 5e-24;
        }
    }

    @Override
    public double getCaptureCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10 * Util.Physics.eV) {
            return 0.00001 * 1e-24;
        } else {
            return 0.002 * 1e-24;
        }
    }
    
    @Override
    public double getTotalCrossSection(double energy) {
        return this.getCaptureCrossSection(energy) + this.getScatterCrossSection(energy);
    }


    // we only need one of these objects
    public static synchronized Carbon getInstance() {
        if (instance == null) {
            Carbon.instance = new Carbon();
        }
        return instance;
    }

}
