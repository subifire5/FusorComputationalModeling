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
public class Environment {

    public static final double limit = 1000; // 1000cm = 10m
    private static Environment instance;
    public LogHistogram counts = new LogHistogram(-10,10,50);
    
    public Environment() {
    }

    public static Environment getInstance() {
        if (Environment.instance == null) {
            Environment.instance = new Environment();
        }
        return Environment.instance;
    }
    
    public static void processEnergy(double e) {
        Environment.getInstance().counts.record(1, e);
    }
    
    public void reset() {
        counts = new LogHistogram(-10,10,50);
    }
}
