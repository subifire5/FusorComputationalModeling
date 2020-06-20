/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.*;

/**
 *
 * @author gunnar
 */
public class Neutron {
    final static double mass = 1.008664; // atomic mass units amu
    final static double startingEnergyDD = 2.45e6; //eV

    double energy; // unit: eV
    Vector3D direction; // no units
    Vector3D position; // unit: (cm,cm,cm)
    Vector3D velocity; // kept in parallel with energy and direction, see set() methods
    
    ArrayList<Event> history = new ArrayList<>();
    
    Neutron(Vector3D position, Vector3D direction, double energy) {
        setPosition(position);
        setDirectionAndEnergy(direction, energy);
    }
    
    public final void setPosition(Vector3D position) {
        this.position = position;
    }

    public final void setVelocity(Vector3D velocity) {
        double speed = velocity.getNorm();
        direction = velocity.normalize();
        
        // should not have to do relativistic calculation since rest energy = 939MeV
        energy = speed * speed * Neutron.mass / 2;
    }
    
    public final void setDirectionAndEnergy(Vector3D direction, double energy) {
        this.direction = direction.normalize();
        this.energy = energy;
        this.velocity = this.direction.scalarMultiply(Math.sqrt(energy*2/Neutron.mass));
    }

    public void processEvent(Event event) {
        //random other particle:
        double particleSpeed = Util.Math.random.nextGaussian() * 
                Math.sqrt(Util.Physics.boltzmann * Util.Physics.roomTemp * 3 / event.element.mass);
        Vector3D particleVelocity = Util.Math.randomDir().scalarMultiply(particleSpeed);

        //establish center of mass
        //add velocity vectors / total mass 
        Vector3D velocityCM = (this.velocity.scalarMultiply(Neutron.mass)
                .add(particleVelocity.scalarMultiply(event.element.mass)))
                .scalarMultiply(1 / (Neutron.mass + event.element.mass));
        
        //convert neutron and particle --> center of mass frame
        this.velocity = this.velocity.subtract(velocityCM);
        //calculate elastic collision: entry speed = exit speed, random direction
        this.velocity = Util.Math.randomDir().scalarMultiply(this.velocity.getNorm());
        //convert back into lab frame
        this.velocity = this.velocity.add(velocityCM);
        
        // update myself (energy and direction)
        this.setVelocity(this.velocity);
        event.energyOut = this.energy;
    }
    

      //replace parameters with 1 Neutron object??
    public void record(Event e) {
        //System.out.println("Neutron"+this.hashCode()+" recording event "+e);
        history.add(e);
    }
}
