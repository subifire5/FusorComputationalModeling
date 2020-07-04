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
public class MYEulersMethod {
        
    EField eField;
    
    public void step (Particle p, Double stepSize) {
       
        p.pos.plusEquals(p.vel.scale(stepSize));
        
        Vector acceleration = eField.forceOnCharge(p).scale(1/p.mass);
        p.vel.plusEquals(acceleration.scale(stepSize));      
       
        p.time += stepSize;
    }
    
    
}
