/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/**
 *
 * @author pjain
 */
public class MYEulersMethod implements Solution {

    EField eField;

    MYEulersMethod(EField eField) {
        this.eField = eField;
    }

    public Particle step(Particle p, Double stepSize) {

        //Particle pReturn = new Particle(p.pos, p.vel, p.polarity, p.time, p.mass);
        p.totalEnergy(eField);

        Vector acceleration = eField.forceOnCharge(p).scale(1 / p.mass);
        //System.out.println("Acceleration: " + acceleration);
        Vector velocity = p.vel.sum(acceleration.scale(stepSize));
        //System.out.println("Velocity: " + p.vel);
        p.pos.plusEquals(p.vel.scale(stepSize));
        //System.out.println("Position: " + p.pos);  

        p.vel = velocity;
        p.time += stepSize;

        return p;
    }

    @Override
    public Particle[] epoch(Particle p, Double stepSize, Double numberOfSteps, int batchSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
