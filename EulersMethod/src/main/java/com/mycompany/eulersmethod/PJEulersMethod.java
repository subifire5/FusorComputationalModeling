/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

import com.mycompany.EulersMethod.EField;
import com.mycompany.EulersMethod.InputHandler;
import com.mycompany.EulersMethod.Particle;
import com.mycompany.EulersMethod.Vector;

/**
 *
 * @author pjain
 */
public class PJEulersMethod implements Solution {   
        EField eField; 
        
        PJEulersMethod(EField efield){
            this.eField = eField;
        }
    
    public Particle step(Particle p, Double stepSize){
        
        Particle bruh = new Particle();
          
        Vector acceleration = eField.forceOnCharge(bruh).scale(1/bruh.mass);
        Vector velocity = acceleration.scale(stepSize).sum(bruh.vel);
        Vector position = bruh.pos.sum(velocity);
        
        bruh.time += stepSize;
        
        return bruh; 
        } 
    
    }
