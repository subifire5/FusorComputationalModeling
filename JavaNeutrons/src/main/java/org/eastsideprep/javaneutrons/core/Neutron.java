/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.*;

/**
 *
 * @author gunnar
 */
public class Neutron {

    final public static double mass = 1.67492749804e-27; // SI for cm
    final public static double startingEnergyDD = 3.925333e-13 * 1e4; //SI for cm
    // factor 1e4 is from using cm, not m here - 100^2

    public double energy; // unit: SI for cm
    public Vector3D direction; // no units
    public Vector3D position; // unit: (cm,cm,cm)
    public Vector3D velocity; // kept in parallel with energy and direction, see set() methods

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
        this.velocity = this.direction.scalarMultiply(Math.sqrt(energy * 2 / Neutron.mass));
    }

    public final void randomizeDirection() {
        this.direction = Util.Math.randomDir();
        this.velocity = this.direction.scalarMultiply(Math.sqrt(energy * 2 / Neutron.mass));
    }

    public void processEvent(Event event) {
        // other particle, velocity following Maxwell-Boltzmann speed distribution
        double particleSpeedComponentSD = Math.sqrt(Util.Physics.boltzmann * Util.Physics.roomTemp / event.element.mass);
        Vector3D particleVelocity = Util.Math.randomGaussianComponentVector(particleSpeedComponentSD);

//        double particleSpeed = particleVelocity.getNorm();
//        double particleEnergy = event.element.mass * particleSpeed * particleSpeed / 2;
//        System.out.println("Particle energy: " + String.format("%6.3e", particleEnergy / Util.Physics.eV));

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

    public void dumpEvents() {
        synchronized (Event.class) {
            history.stream().forEach(event -> System.out.println(event));
        }
    }
}
