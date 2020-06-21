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
public class Oxygen extends Element {

    private static Oxygen instance;

    Oxygen() {
        super("16-Oxygen", 8, 8);
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

    // we only need one of these objects
    public static synchronized Oxygen getInstance() {
        if (instance == null) {
            Oxygen.instance = new Oxygen();
        }
        return instance;
    }

}
