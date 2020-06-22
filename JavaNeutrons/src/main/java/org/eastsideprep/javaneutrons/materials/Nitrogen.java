/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Element;

/**
 *
 * @author gunnar
 */
public class Nitrogen extends Element {

    private static Nitrogen instance;

    Nitrogen() {
        super("14-Nitrogen", 7, 7);
    }

    @Override
    public double getScatterCrossSection(double energy) {
        // todo: get real table data
        return 11.53e-24;
    }

    @Override
    public double getCaptureCrossSection(double energy) {
        // todo: get real table data
        return 1.91 * 1e-24;
    }

    // we only need one of these objects
    public static synchronized Nitrogen getInstance() {
        if (instance == null) {
            Nitrogen.instance = new Nitrogen();
        }
        return instance;
    }

}
