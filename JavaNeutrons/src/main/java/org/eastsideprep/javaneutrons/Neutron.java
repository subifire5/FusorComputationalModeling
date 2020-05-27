/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.threed.*;
/**
 *
 * @author gunnar
 */
public class Neutron {
    double energy; // unit: eV
    Vector3D unitvector; // no units
    Vector3D position; // unit: (cm,cm,cm)
    // history
    
    Random rand = new Random();
    final double boltzmann = 8.61733333353e-5; //eV/K
    final double roomTemp = 293.0; //K
    final double neutronMass = 1.008664; // atomic mass units amu
    final double protonMass = 1.007276; // amu
    final double startingEnergy = 2.45e6; //eV
    
    public Vector3D randomDir(){
        double theta = (rand.nextDouble()*2*Math.PI);
        double phi = Math.acos(rand.nextDouble()*2*Math.PI - 1);
        return new Vector3D(Math.cos(theta)*Math.sin(phi), Math.sin(theta)*Math.sin(phi), Math.cos(phi));        
    }
    
    public double pathLength(double sigma){
        return -Math.log(rand.nextDouble()) / sigma;
    }
    
    public Vector3D elasticScatter(Vector3D neutronVelocity, Element particle){
        //random other particle:
        double particleSpeed = rand.nextGaussian()*Math.sqrt(boltzmann*roomTemp*3/particle.mass);
        Vector3D particleVelocity = randomDir().scalarMultiply(particleSpeed);
        
        //establish center of mass
        //add velocity vectors / total mass 
        Vector3D velocityCM = (neutronVelocity.scalarMultiply(neutronMass)
                .add(particleVelocity.scalarMultiply(particle.mass)))
                .scalarMultiply(1/(neutronMass + particle.mass));
        //convert neutron and particle --> center of mass frame
        neutronVelocity = neutronVelocity.subtract(velocityCM);
        //calculate elastic collision
        neutronVelocity = randomDir().scalarMultiply(neutronVelocity.getNorm());       
        //convert into lab frame
        neutronVelocity = neutronVelocity.add(velocityCM);
        //return results
        return neutronVelocity;
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
