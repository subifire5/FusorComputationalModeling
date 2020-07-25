/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mycompany.EulersMethod;

/**
 *
 * @author pjain
 */

//import com.mycompany.EulersMethod.EField;
//import com.mycompany.EulersMethod.InputHandler;
//import com.mycompany.EulersMethod.PJEulersMethod;

/*public class PJRungeKutta implements Solution {

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
    public Particle j(Particle p, double stepSize) {
        
        Vector k1 = j.pos.sum(j.vel);
      
        Vector k1_first3 = p.vel;
        Vector k1_last3 = eField.forceOnCharge(p).scale(1/p.mass);
        Particle j = p.clone();
        j.pos = k1_first3;
        j.vel = k1_last3;
        Vector k1 = j.pos.sum(j.vel);
        

        Vector k2_first3 = j.pos.sum(stepSize*(k1.scale(0.5)));
        Vector k2_last3 = (eField.forceOnCharge(j).scale(1/j.mass)).sum(stepSize*(k1.scale(0.5)));
        Vector k2 = k2_first3.sum(k2_last3); 
        
        Vector k3_first3 = j.pos.sum(stepSize*(k2.scale(0.5)));
        Vector k3_last3 = 
        double time = p.time += stepSize;
        p.time = time; 
        
        
       // PJEulersMethod pj = new PJEulersMethod(eField);
        
       /* Particle kp1 = pj.step(p.clone(),stepSize);
        Vector k1 = new Vector(kp1.pos);
        Double k2_t =  p.time + (stepSize/2);
        Double k2_x = p.pos.x + (stepSize*(kp1.pos.x/2)); 
        Double k2_y = p.pos.y + (stepSize*(kp1.pos.y/2)); 
        Double k2_z = p.pos.z + (stepSize*(kp1.pos.z/2));
        Vector k2 = new Vector(k2_x, k2_y, k2_z);
        Double k3_t = p.time + (stepSize/2);
        Double k3_x = p.pos.x + (stepSize*(k2_x/2));
        Double k3_y = p.pos.y + (stepSize*(k2_y/2));
        Double k3_z = p.pos.z + (stepSize*(k2_z/2));
        Vector k3 = new Vector(k3_x, k3_y,k3_z);
        Double k4_t = p.time + stepSize;
        Double k4_x = p.pos.x + (stepSize*k3_x);
        Double k4_y = p.pos.y + (stepSize*k3_y);
        Double k4_z = p.pos.z + (stepSize*k3_z);
        Vector k4 = new Vector(k4_x, k4_y, k4_z);
        //Vector newpos = new Vector (k4_x + stepSize, k4_y + stepSize*(k1.sum(2.scale(k2).sum(2.scale(k3.sum(k4)))),k4_z);*/
                

    /*}

    @Override
    public Particle[] epoch(Particle p, Double stepSize, Double numberOfSteps, int batchSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Particle step(Particle p, Double stepSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
    */

