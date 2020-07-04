package org.eastsideprep.javaneutrons.core;

import java.util.ArrayList;
import java.util.concurrent.LinkedTransferQueue;
import javafx.scene.Node;
import org.apache.commons.math3.geometry.euclidean.threed.*;

public class Neutron {

    final public static double mass = 1.67492749804e-27; // SI
    final public static double startingEnergyDD = 3.925333e-13 * 1e4; //SI for cm
    // factor 1e4 is from using cm, not m here - 100^2

    public double energy; // unit: SI for cm
    public Vector3D direction; // no units
    public Vector3D position; // unit: (cm,cm,cm)
    public Vector3D velocity; // kept in parallel with energy and direction, see set() methods
    public double entryEnergy = 0;
    public double totalPath = 0;
    public MonteCarloSimulation mcs;

    ArrayList<Event> history = new ArrayList<>();

    public Neutron(Vector3D position, Vector3D direction, double energy, MonteCarloSimulation mcs) {
        this.position = position;
        setDirectionAndEnergy(direction, energy);
        this.mcs = mcs;
    }

    public final void setPosition(LinkedTransferQueue<Node> q, Vector3D position) {
        Vector3D oldPosition = this.position;
        this.position = position;

        this.totalPath += position.subtract(oldPosition).getNorm();
        if (this.mcs.trace) {
            Util.Graphics.drawLine(q, oldPosition, position, 0.1, this.energy);
        }
    }

    public final void setPosition(Vector3D newPosition) {
        if (this.position != null) {
            this.totalPath += newPosition.subtract(this.position).getNorm();
        }
        this.position = newPosition;
    }

    public final void setVelocity(Vector3D velocity) {
        double speed = velocity.getNorm();
        this.direction = velocity.normalize();

        // should not have to do relativistic calculation since rest energy = 939MeV
        this.energy = speed * speed * Neutron.mass / 2;
        this.velocity = velocity;
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

     public Vector3D getScatteredVelocity(Isotope i, Vector3D neutronVelocity) {
        if (i.angles != null) {
            double neutronSpeed = neutronVelocity.getNorm();
            double energyEV = 0.5 * Neutron.mass * neutronSpeed * neutronSpeed/Util.Physics.eV;
            
            // get the scattering angle from a random lookup in the tables
            double cos_theta = i.getScatterCosTheta(energyEV);
        
            // construct vector and return
            Vector3D v = Util.Math.randomDir(cos_theta, neutronSpeed);
            Rotation r = new Rotation(Vector3D.PLUS_K, neutronVelocity);
            v = r.applyTo(v);
           
            //System.out.println("v: " + v);
            return v;

        } else {
            return Util.Math.randomDir().scalarMultiply(neutronVelocity.getNorm());
        }
    }
    
    public void processEvent(Event event) {
        if (event.code == Event.Code.Scatter) {
            // other particle, velocity following Maxwell-Boltzmann speed distribution
            // derived through: <Ex> = 0.5*kB*T (x component takes a third of the kinetic energy)
            // => 0.5*m*<vx^2> = 0.5*kB*T
            // with <vx^2> = var(vx)
            // => m*sd(vx) = sqrt(kB*T)
            // => sd(vx) = sd(vy) = sd(vz) = sqrt(kB*T/m)
            double particleSpeedComponentSD = Math.sqrt(Util.Physics.boltzmann * Util.Physics.roomTemp / event.element.mass);
            Vector3D particleVelocity = Util.Math.randomGaussianComponentVector(particleSpeedComponentSD);

            // making these for later debug out
            double particleSpeed = particleVelocity.getNorm();
            double particleEnergy = event.element.mass * particleSpeed * particleSpeed / 2;
            double neutronSpeed = this.velocity.getNorm();
            double neutronEnergyIn = this.energy;

            //establish center of mass
            //add velocity vectors / total mass 
            Vector3D velocityCM = (this.velocity.scalarMultiply(Neutron.mass)
                    .add(particleVelocity.scalarMultiply(event.element.mass)))
                    .scalarMultiply(1 / (Neutron.mass + event.element.mass));
            double speedCM = velocityCM.getNorm();

            //convert neutron and particle --> center of mass frame
            Vector3D velocityNCM = this.velocity.subtract(velocityCM);
            //calculate elastic collision: entry speed = exit speed, random direction
            //velocityNCM = Util.Math.randomDir().scalarMultiply(velocityNCM.getNorm());
            double speedNCM = velocityNCM.getNorm();
            velocityNCM = this.getScatteredVelocity(event.element, velocityNCM);
            //double neutronSpeedCM = velocityNCM.getNorm();
            //convert back into lab frame
            Vector3D velocityNLab = velocityNCM.add(velocityCM);

            // update myself (energy and direction)
            double angle = Math.acos(this.velocity.normalize().dotProduct(velocityNLab.normalize())) / Math.PI * 180;
            double angleWithX = Math.acos(Vector3D.PLUS_I.dotProduct(velocityNLab.normalize())) / Math.PI * 180;
            this.setVelocity(velocityNLab);
            event.energyOut = this.energy;
            if (this.mcs != null && this.mcs.trace) {
                this.mcs.scatter = true;
                synchronized (Neutron.class) {
                    System.out.println("Neutron: " + this.hashCode());
                    System.out.println(" Particle: " + event.element.name);
                    System.out.println(" Particle energy: " + String.format("%6.3e eV", particleEnergy / Util.Physics.eV));
                    System.out.println(" Neutron energy in: " + String.format("%6.3e eV", neutronEnergyIn / Util.Physics.eV));
                    System.out.println(" Speed of Neutron: " + String.format("%6.3e cm/s", neutronSpeed));
                    System.out.println(" Speed of Particle: " + String.format("%6.3e cm/s", particleSpeed));
                    System.out.println(" Speed of CM: " + String.format("%6.3e cm/s", speedCM));
                    System.out.println(" Neutron energy out: " + String.format("%6.3e eV", this.energy / Util.Physics.eV));
                    System.out.println(" Scattering angle: " + angle);
                    System.out.println(" Scattering angle with x-axis: " + angleWithX);
                    System.out.println(" Velocity unit dir " + this.velocity.normalize());
                    //Util.Graphics.drawLine(this.mcs.visualizations, position, position.add(velocity.normalize().scalarMultiply(45)), 1, Color.BLACK);
                }
            }
        } else if (event.code == Event.Code.Capture) {
            // capture
            Environment.recordCapture();
        }
    }

    //replace parameters with 1 Neutron object??
    public boolean record(Event e) {
        //System.out.println("Neutron"+this.hashCode()+" recording event "+e);
        history.add(e);
        if (history.size() > 1000) {
            dumpEvents();
            return false;
        }
        return true;
    }

    public void dumpEvents() {
        synchronized (Event.class) {
            System.out.println("");
            System.out.println("-- start of neutron events:");
            history.stream().forEach(event -> System.out.println(event));
            System.out.println("-- done");
            System.out.println("");
        }
    }
}
