/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class MonteCarloSimulation {
    
    Random rand = new Random();
    final double boltzmann = 8.61733333353e-5; //eV/K
    final double roomTemp = 293.0; //K
    final double neutronMass = 1.008664; // atomic mass units amu
    final double protonMass = 1.007276; // amu
    final double startingEnergy = 2.45e6; //eV
    
    public Vector3D randomDir(){
        float theta = (float) (rand.nextFloat()*2*Math.PI);
        float phi = (float) Math.acos(rand.nextFloat()*2*Math.PI - 1);
        return new Vector3D(Math.cos(theta)*Math.sin(phi), Math.sin(theta)*Math.sin(phi), Math.cos(phi));        
    }
    
    public float pathLength(float sigma){
        return (float) Math.log(rand.nextDouble()) / sigma*(-1);
    }
    
    public Vector3D elasticScatter(Vector3D neutronDir, float neutronEnergy){ //double or float?
        double protonEnergy = rand.nextGaussian() * boltzmann * roomTemp / 2;
        Vector3D protonDir = randomDir();
        
        Vector3D neutron = neutronDir.scalarMultiply(neutronEnergy);
        Vector3D proton = protonDir.scalarMultiply(protonEnergy);
        
        Vector3D referenceFrame = (neutron.scalarMultiply(neutronMass).add(proton.scalarMultiply(protonMass))).scalarMultiply(1 / (neutronMass + protonMass));        
        Vector3D neutronRef = neutron.subtract(referenceFrame);
        //where is the length method?? https://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/geometry/euclidean/threed/Vector3D.html
        Vector3D scatteredRef = randomDir(); //.scalarMultiply(neutronRef.length());
        Vector3D scattered = scatteredRef.add(referenceFrame);
        
        return scattered; //to be broken down into normalized vector and magnitude
    }
    
    //replace parameters with 1 Neutron object??
    public void recordEscape (int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy){
        //increase count of escaped neutrons
        //log.add(new Event(ESCAPE, neutronNum, eventNum, pos, dir, energy, energy));
    }
      
    public void recordElasticScatter (int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy1, float energy2){
        //increase count of scattered neutrons
        //log.add(new Event(SCATTER, neutronNum, eventNum, pos, dir, energy1, energy2));
    }
    
    public void recordAbsorb (int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy){
        //increase count of absorbed neutrons
        //log.add(new Event(ABSORB, neutronNum, eventNum, pos, dir, energy, energy))
    }
    
    //this function was used to account for 'lost' neutrons within blocks (glitches)
    public void recordEscapeObj (int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy){
        //increase count of escaped neutrons from object / lost neutrons
        //log.add(new Event(LOST, neutronNum, eventNum, pos, dir, energy, energy));
    }
    
    
    //replace parameters w Neutron object?
    public void scatter(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy, Part inside){ //do we want energy as double or float?
        //if not inside a part (inside == null):
            // intersection = scene.intersectScene in f#
        //if inside the part:
            // intersection = scene.intersectObj(inside) in f#
            
        //if no intersection (intersection == null):
            //if (inside==null) then recordEscape
            //if (inside!=null) then recordEscapeObj (lost/glitched neutron)
        //if some intersection:
            //if (inside==null) then scatter (recurse with new inside parameter for the part it is in)
            //if (inside != null) then we do some stuff (??)
                //find cross sections and compare to energy and current intersection/inside info
                //could end in elastic scatter, absorb, or recurse
    }

}
