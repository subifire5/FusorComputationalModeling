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
    Vector3D direction; // no units
    Vector3D position; // unit: (cm,cm,cm)
    Vector3D velocity; // kept in parallel with energy and direction, see set() methods

    final static double mass = 1.008664; // atomic mass units amu
    final static double startingEnergy = 2.45e6; //eV

    public void setVelocity(Vector3D velocity) {
        double speed = velocity.getNorm();
        direction = velocity.normalize();
        // todo: relativistic?
        energy = speed * speed * Neutron.mass / 2;
    }
    
    public void setDirectionAndEnergy(Vector3D direction, double energy) {
        this.direction = direction.normalize();
        this.energy = energy;
        this.velocity = this.direction.scalarMultiply(Math.sqrt(energy*2/Neutron.mass));
    }

    public Vector3D elasticScatter(Element particle) {
        //random other particle:
        double particleSpeed = Util.random.nextGaussian() * Math.sqrt(Physics.boltzmann * Physics.roomTemp * 3 / particle.mass);
        Vector3D particleVelocity = Physics.randomDir().scalarMultiply(particleSpeed);

        //establish center of mass
        //add velocity vectors / total mass 
        Vector3D velocityCM = (this.velocity.scalarMultiply(Neutron.mass)
                .add(particleVelocity.scalarMultiply(particle.mass)))
                .scalarMultiply(1 / (Neutron.mass + particle.mass));
        
        //convert neutron and particle --> center of mass frame
        this.velocity = this.velocity.subtract(velocityCM);
        //calculate elastic collision: entry speed = exit speed, random direction
        this.velocity = Physics.randomDir().scalarMultiply(this.velocity.getNorm());
        //convert back into lab frame
        this.velocity = this.velocity.add(velocityCM);
        
        // update myself (energy and direction)
        this.setVelocity(this.velocity);
        //return results
        return this.velocity;
    }

    //replace parameters with 1 Neutron object??
    public void recordEscape(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy) {
        //increase count of escaped neutrons
        //log.add(new Event(ESCAPE, neutronNum, eventNum, pos, dir, energy, energy));
    }

    public void recordElasticScatter(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy1, float energy2) {
        //increase count of scattered neutrons
        //log.add(new Event(SCATTER, neutronNum, eventNum, pos, dir, energy1, energy2));
    }

    public void recordAbsorb(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy) {
        //increase count of absorbed neutrons
        //log.add(new Event(ABSORB, neutronNum, eventNum, pos, dir, energy, energy))
    }

    //this function was used to account for 'lost' neutrons within blocks (glitches)
    public void recordEscapeObj(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy) {
        //increase count of escaped neutrons from object / lost neutrons
        //log.add(new Event(LOST, neutronNum, eventNum, pos, dir, energy, energy));
    }

    //replace parameters w Neutron object?
    public void scatter(int neutronNum, int eventNum, Vector3D pos, Vector3D dir, float energy, Part inside) { //do we want energy as double or float?
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
