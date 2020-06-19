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
public class Environment extends Detector {

    public static final double limit = 10000; // cm
    private static Environment instance;
    
    public Environment() {
        super("Environment");
    }

    private static Environment getInstance() {
        if (Environment.instance == null) {
            Environment.instance = new Environment();
        }
        return Environment.instance;
    }
    
    public static void processEnergy(double e) {
        Environment.getInstance().processEntryEnergy(e);
    }
}
