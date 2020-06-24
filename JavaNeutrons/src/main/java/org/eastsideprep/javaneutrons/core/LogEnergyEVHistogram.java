/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

/**
 *
 * @author gunnar
 */
public class LogEnergyEVHistogram extends LogHistogram {

    @Override
    public void record(double value, double energy) {
        super.record(value, energy / Util.Physics.eV);
    }
}
