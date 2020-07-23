package org.eastsideprep.javaneutrons.core;

import org.apache.commons.math3.geometry.euclidean.threed.*;

public final class Neutron extends Particle {

    public static double totalPE = 0.0;
    public static int countPE = 0;
    public static double totalNE = 0.0;
    public static int countNE = 0;
    public static double totalNE2 = 0.0;
    public static int countNE2 = 0;

    final public static double mass = 1.67492749804e-27; // SI
    final public static double startingEnergyDD = 3.925333e-13 * 1e4; //SI for cm
    // factor 1e4 is from using cm, not m here - 100^2

    public Vector3D velocity; // kept in parallel with energy and direction, see set() methods

    public Neutron(Vector3D position, Vector3D direction, double energy, MonteCarloSimulation mcs) {
        super(position, direction, energy, mcs);
    }

    public final void setVelocity(Vector3D velocity) {
        double speed = velocity.getNorm();
        this.direction = velocity.normalize();

        // should not have to do relativistic calculation since rest energy = 939MeV
        this.energy = speed * speed * Neutron.mass / 2;
        this.velocity = velocity;
    }

    @Override
    public void setDirectionAndEnergy(Vector3D direction, double energy) {
        this.direction = direction.normalize();
        this.energy = energy;
        this.velocity = this.direction.scalarMultiply(Math.sqrt(energy * 2 / Neutron.mass));
    }

    public Vector3D getScatteredVelocity(Event e, Vector3D neutronVelocity) {
        Isotope i = e.particle;
        if (i.angles != null) {
            double neutronSpeed = neutronVelocity.getNorm();
            double energyEV = 0.5 * Neutron.mass * neutronSpeed * neutronSpeed / Util.Physics.eV;

            // get the scattering angle from a random lookup in the tables
            e.cos_theta = i.getScatterCosTheta(energyEV);

            // construct vector and return
            Vector3D v = Util.Math.randomDir(e.cos_theta, neutronSpeed);
            // random vector was scattered around Z, rotate to match axis of incoming neutron
            Rotation r = new Rotation(Vector3D.PLUS_K, neutronVelocity);
            v = r.applyTo(v);

            //System.out.println("v: " + v);
            return v;

        } else {
           return getIsotropicScatteredVelocity(e, neutronVelocity);
        }
    }
   public Vector3D getIsotropicScatteredVelocity(Event e, Vector3D neutronVelocity) {
        Isotope i = e.particle;
        boolean mncpMethod = false;
            double speed = neutronVelocity.getNorm();
            Vector3D v = Util.Math.randomDir();
            e.cos_theta = v.dotProduct(neutronVelocity.scalarMultiply(1/speed));
            v = v.scalarMultiply(speed);
            return v;
    }
    @Override
    public void processEvent(Event event) {
        if (event.code == Event.Code.Scatter) {
            // other particle, velocity following Maxwell-Boltzmann speed distribution
            // derived through: <Ex> = 0.5*kB*T (x component takes a third of the kinetic energy)
            // => 0.5*m*<vx^2> = 0.5*kB*T
            // => m<vx^2> = kB*T
            // with <vx^2> = var(vx) because mean(vx) = 0
            // => m*var(vx) = kB*T
            // => var(vx) = kB*t/m
            // => sd(vx) = sd(vy) = sd(vz) = sqrt(kB*T/m)
            double particleSpeedComponentSD = Math.sqrt(Util.Physics.kB * Util.Physics.T / event.particle.mass);
            Vector3D particleVelocityIn = Util.Math.randomGaussianComponentVector(particleSpeedComponentSD);
            double particleSpeedIn = particleVelocityIn.getNorm();

            // remembering these for later debug out
            Vector3D velocityIn = this.velocity;
            double energyIn = this.energy;

            //establish center of mass
            //add velocity vectors / total mass 
            Vector3D velocityCM = (this.velocity.scalarMultiply(Neutron.mass)
                    .add(particleVelocityIn.scalarMultiply(event.particle.mass)))
                    .scalarMultiply(1 / (Neutron.mass + event.particle.mass));

            //convert neutron and particle --> center of mass frame
            Vector3D neutronVelocityCM = this.velocity.subtract(velocityCM);
            //calculate elastic collision: entry speed = exit speed, random direction
            neutronVelocityCM = this.getScatteredVelocity(event, neutronVelocityCM);

            // debug: process the scatter for the incident particle as well
            // not needed IRL
            
            event.particleEnergyIn = event.particle.mass * particleSpeedIn * particleSpeedIn / 2;
            Vector3D particleVelocityCM = particleVelocityIn.subtract(velocityCM);
            particleVelocityCM = neutronVelocityCM.normalize().scalarMultiply(-particleVelocityCM.getNorm());
            Vector3D particleVelocityOut = particleVelocityCM.add(velocityCM);
            double particleSpeedOut = particleVelocityOut.getNorm();
            event.particleEnergyOut = event.particle.mass * particleSpeedOut * particleSpeedOut / 2;

            //convert neutron back into lab frame
            Vector3D velocityOut = neutronVelocityCM.add(velocityCM);
            // update neutron energy and direction
            this.setVelocity(velocityOut);
            event.energyOut = this.energy;
            
            double aPercentloss = ((event.particleEnergyOut + this.energy) - (event.particleEnergyIn + energyIn))/
                    (event.particleEnergyIn + energyIn)*100;

            if (this.mcs != null && this.mcs.traceLevel >= 2) {
                this.mcs.scatter = true;
                double particleSpeed = particleVelocityIn.getNorm();
                double particleEnergy = event.particle.mass * particleSpeed * particleSpeed / 2;
                double neutronSpeed = velocityIn.getNorm();
                double speedCM = velocityCM.getNorm();
                double speedNCM = neutronVelocityCM.getNorm();
                double neutronSpeedCM = neutronVelocityCM.getNorm();
                double angle = Math.acos(velocityOut.normalize().dotProduct(velocityIn.normalize())) / Math.PI * 180;
                double angleWithX = Math.acos(velocityOut.dotProduct(Vector3D.PLUS_I.normalize())) / Math.PI * 180;
                synchronized (Neutron.class) {
                    System.out.println("Neutron: " + this.hashCode());
                    System.out.println(" Particle: " + event.particle.name);
                    System.out.println(" Particle energy: " + String.format("%6.3e eV", particleEnergy / Util.Physics.eV));
                    System.out.println(" Neutron energy in: " + String.format("%6.3e eV", energyIn / Util.Physics.eV));
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
}
