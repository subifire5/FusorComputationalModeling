/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

/**
 *
 * @author gunnar
 */
public class Unobtainium extends Element {

    private static Unobtainium instance;
    
    Unobtainium() {
        // basically, hydrogen with really large, constant cross-section
        super("Unobtainium", Physics.protonMass, 1, 0);
    }

    @Override
    public double getCrossSection(double energy) {
        return 1;
    }

    
        // we only need one of these objects
    public static Unobtainium getInstance() {
        if (instance == null) {
            Unobtainium.instance = new Unobtainium();
        }
        return instance;
    }

}
