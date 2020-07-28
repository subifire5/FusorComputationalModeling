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

import com.mycompany.EulersMethod.EField;
import com.mycompany.EulersMethod.InputHandler;
import com.mycompany.EulersMethod.PJEulersMethod;

public class PJRungeKutta implements Solution {

    EField eField;

    PJRungeKutta(EField eField) {
        this.eField = eField;
    }

    public Particle f(Particle p, Double stepSize){
        
        Vector first3 = p.vel;
        Vector last3 = eField.forceOnCharge(p).scale(1/p.mass);
        Particle j = p.clone();
        j.pos = first3;
        j.vel = last3;
        
        return j;

    }
    @Override
    public Particle step(Particle p, Double stepSize) {
        
        int x = 2;
        double two = (double) x;
        
        Particle k1 = f(p,stepSize);
        Particle k1_2 = p.clone();
        k1_2.pos.scale(stepSize/2);
        k1_2.vel.scale(stepSize/2);
        k1_2.pos.sum(p.pos);
        k1_2.vel.sum(p.vel);
        Particle k2 = f(k1_2,stepSize);
        
        Particle k2_3= p.clone();
        k2_3.pos.scale(stepSize/2);
        k2_3.vel.scale(stepSize/2);
        k2_3.pos.sum(p.pos);
        k2_3.vel.sum(p.vel);
        Particle k3 = f(k2_3,stepSize);
        
        Particle k3_4 = p.clone();
        k3_4.pos.scale(stepSize);
        k3_4.vel.scale(stepSize);
        k3_4.pos.sum(p.pos);
        k3_4.pos.sum(p.vel);
        Particle k4 = f(k3_4,stepSize);
                
        Particle p2 = p.clone();
        
        p2.pos.plusEquals(k1.pos.sum(k2.pos.scale(two)).sum(k3.pos.scale(two).sum(k4.pos).scale(1/6*stepSize)));
        p2.vel.plusEquals(k1.vel.sum(k2.vel.scale(two)).sum(k3.vel.scale(two).sum(k4.vel).scale(1/6*stepSize)));
        
        p2.time += stepSize;
        
        return p2;
    }
        

    @Override
    public Particle[] epoch(Particle p, Double stepSize, Double numberOfSteps, int batchSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}