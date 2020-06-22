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
public class Iron extends Element {

    private static Iron instance;

    Iron() {
        super("56-Iron", 26, 30);
    }

    @Override
    public double getScatterCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10 * Util.Physics.eV) {
            return 20e-24;
        } else {
            return 0.003e-24;
        }
    }

    @Override
    public double getCaptureCrossSection(double energy) {
        // todo: get real table data
        if (energy > 10 * Util.Physics.eV) {
            return 2 * 1e-24;
        } else {
            return 10 * 1e-24;
        }
    }

    @Override
    public double getTotalCrossSection(double energy) {
        return this.getCaptureCrossSection(energy) + this.getScatterCrossSection(energy);
    }

    // we only need one of these objects
    public static synchronized Iron getInstance() {
        if (instance == null) {
            Iron.instance = new Iron();
        }
        return instance;
    }

}
