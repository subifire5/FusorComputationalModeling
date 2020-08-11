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

    public Particle f(Particle p){
        
        Vector first3 = p.vel;
        Vector last3 = eField.forceOnCharge(p).scale(1/p.mass);
        Particle j = p.clone();
        j.pos = first3;
        j.vel = last3;
        
        return j;

    }
    @Override
    public Particle step(Particle p, Double stepSize) {
           
        p.totalEnergy(eField);
        Particle k1 = f(p.clone());
        Particle k1_2 = p.clone();
        k1_2.plusEquals(k1.clone().multiply(stepSize/2));
        Particle k2 = f(k1_2);

        Particle k2_3 = p.clone();
        k2_3.plusEquals(k2.clone().multiply(stepSize/2));
        Particle k3 = f(k2_3);
        
        Particle k3_4 = p.clone();
        k3_4.plusEquals(k3.clone().multiply(stepSize));
        Particle k4 = f(k3_4);
                
        Particle p2 = p.clone();
        
        k2_3.multiply(2.0);
        k3_4.multiply(2.0);
        
        Particle[] k5 = {k1,k2,k3,k4};
        p2.plusEquals(k5);
        p2.multiply(stepSize/6);     
        p2.time += stepSize;
        
        return p2;
    }
        

    @Override
    public Particle[] epoch(Particle p, Double stepSize, Double numberOfSteps, int batchSize) {
       
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   
        int batchesNeeded = (int) (numberOfSteps/batchSize);
        int batchesCompleted = 0;
        int steps = 0;
        Particle[] j = new Particle[batchesNeeded];
        long startTime = System.currentTimeMillis();
        Double completed = (double)batchesCompleted;
        Double needed = (double)batchesNeeded;
        

        while (batchesCompleted != batchesNeeded) {
            System.out.println("Batches Completed: " + batchesCompleted);
            System.out.println("Batches Needed: " + batchesNeeded);
            System.out.println("Overall Progress: " + ((completed/needed)*100) + "%");
            System.out.println("This code has taken: " + ((System.currentTimeMillis() - startTime)/1000) + " seconds to process.");

            if (steps < batchSize) {
                step(p, stepSize);
                steps++;
            } else if (steps == batchSize) {
                steps = 0;
                j[batchesCompleted] = p.clone();
                batchesCompleted++;
                completed++;
            }

        }
        return j;
    }
    }


