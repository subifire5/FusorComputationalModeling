/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/**
 *
 * @author myan
 */
public class MYRungeKutta implements Solution{
    
    EField eField;
    
    MYRungeKutta(EField eField) {
        this.eField = eField;
    }
    
    public Particle f(Particle p) {
        Vector first3 = p.vel;
        Vector last3 = eField.forceOnCharge(p).scale(1/p.mass);
        Particle p1 = p.clone();
        p1.pos = first3;
        p1.vel = last3;
        return p1;
    }
    @Override
    public Particle step(Particle p, Double stepSize) {
        Particle k1 = f(p.clone());
        Particle p1 = p.clone();
        p1.pos.plusEquals(k1.clone().pos.scale(stepSize/2));
        p1.vel.plusEquals(k1.clone().vel.scale(stepSize/2));
        Particle k2 = f(p1);
        Particle p2 = p.clone();
        p2.pos.plusEquals(k2.clone().pos.scale(stepSize/2));
        p2.vel.plusEquals(k2.clone().vel.scale(stepSize/2));
        Particle k3 = f(p2);
        Particle p3 = p.clone();
        p3.pos.plusEquals(k3.clone().pos.scale(stepSize));
        p3.vel.plusEquals(k3.clone().pos.scale(stepSize));
        Particle k4 = f(p3);
        
        Particle p4 = p.clone();
        p4.vel.plusEquals((((k1.vel.sum(k2.vel.scale(2.0))).sum(k3.vel.scale(2.0))).sum(k4.vel)).scale(1/6*stepSize));
        p4.pos.plusEquals((((k1.pos.sum(k2.pos.scale(2.0))).sum(k3.pos.scale(2.0))).sum(k4.pos)).scale(1/6*stepSize));
        p4.time += stepSize;
        
        return p4;        
    }

    @Override
    public Particle[] epoch(Particle p, Double stepSize, Double numberOfSteps, int batchSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
