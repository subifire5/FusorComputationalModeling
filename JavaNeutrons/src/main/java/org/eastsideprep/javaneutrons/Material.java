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
public class Material {

    public class Component {

        Element e;
        double density; // atoms/(barn*cm)
    }

    String name;
    ArrayList<Component> components;

    // compute macroscopic cross-section
    public double getSigma(double energy) {
        double sigma = 0;
        for (Component c : components) {
            sigma += c.e.getCrossSection(energy) * c.density;
        }
        return sigma;
    }

    public double randomPathLength(double energy) {
        return -Math.log(Util.random.nextDouble()) / getSigma(energy);
    }
    
 
    public Event nextPoint(Neutron n) {
        double t = randomPathLength(n.energy);
        Vector3D location = n.position.add(n.direction.scalarMultiply(t));
        // todo:
        // make array of sigmas for this energy, for all elements in here
        // make parallel array of cumulative sums
        // include both absortion and scattering cross-sections
        // make random number between 0 and sum of sigmas
        // bsearch to find the corresponding index
        // cop-out: use first element
        Element e = components.get(0).e;
        
        // todo: from bsearch index, also decide whether this was scatter or absorp
        // cop-out: scatter:
        Event.Code c = Event.Code.Scatter;
        
        return new Event(location, c, t, n, e);
    }

}
