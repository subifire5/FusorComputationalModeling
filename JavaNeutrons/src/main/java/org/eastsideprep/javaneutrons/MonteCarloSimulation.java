/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class MonteCarloSimulation {
    Assembly assembly;
    Vector3D origin;
    
    MonteCarloSimulation(Assembly assembly, Vector3D origin) {
        this.assembly = assembly;
        this.origin = origin;
    }
    
    ArrayList<Neutron>  simulateNeutrons(long count) {
        ArrayList<Neutron> neutrons = new ArrayList<>();
        for (long i = 0; i<count; i++) {
            neutrons.add(new Neutron(Vector3D.ZERO, Physics.randomDir(), 2.5e6));
        }
        
        neutrons.stream().forEach(n->simulateNeutron(n));
        return neutrons;
    }
    
    void simulateNeutron(Neutron n) {
        // todo: include vacuum
        this.assembly.evolveNeutronPathNoVacuum(n);
    }

}
