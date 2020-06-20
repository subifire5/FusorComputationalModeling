/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class Material {
    
    static HashMap<String, Material> materials = new HashMap<>();

    public class Component {

        Element e;
        double density; // atoms/(barn*cm)
        
        Component(Element e, double density) {
            this.e = e;
            this.density = density;
        }
    }

    String name;
    ArrayList<Component> components;
    
    public Material(String name) {
        materials.put(name, this);
        components = new ArrayList<>();
    }
    
    public static Material getByName(String name) {
        return materials.get(name);
    }
    
    public void addComponent(Element element, double density) {
        components.add(new Component(element, density));
    }

    // compute macroscopic cross-section
    private double getSigma(double energy) {
        double sigma = 0;
        for (Component c : components) {
            sigma += c.e.getCrossSection(energy) * c.density;
        }
        return sigma;
    }

    private  double randomPathLength(double energy) {
        return -Math.log(Util.Math.random.nextDouble()) / getSigma(energy);
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
        
        return new Event(location, c, t, e);
    }

}
