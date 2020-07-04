/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

import java.util.ArrayList;
/**
 *
 * @author pjain
 */
public class Main {

    public static void main(String[] args) {
        InputHandler ih = new InputHandler();
        ih.getInput();
        EulersMethod em = new EulersMethod(ih.eField);
        Particle p = new Particle();
        //ArrayList<Vector> particleList = new ArrayList<Vector>();
        //for (int i = 0; i < 100; i++) {
        System.out.println("position: " +p);
        em.step(p, 1E-20); 
        System.out.println("position of p:" + p);
        //particleList.add(p.pos);
        }
    }        

    public static void times2(Double d){
        d=d*2;
    }
}
