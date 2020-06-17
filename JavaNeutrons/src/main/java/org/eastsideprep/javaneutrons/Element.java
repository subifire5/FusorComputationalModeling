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
public class Element {

    String name;
    double mass; // g
    int atomicNumber;
    int neutrons;

    Element(String name, double mass, int atomicNumber, int neutrons) {
        // todo fill this in
    }

    public double getCrossSection(double energy) {
        // todo: this is Taras' job
        return 0;
    }
}
