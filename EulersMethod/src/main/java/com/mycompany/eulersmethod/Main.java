/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author pjain
 */
public class Main {

    public static void main(String[] args) {
        InputHandler ih = new InputHandler();
        ih.getInput();
        
        Vector test1 = new Vector (0.0, 0.0, 0.0);
        Vector test2 = new Vector (1000.0, 1000.0, 1000.0);
        System.out.println("Grid vector: " + ih.eField.electricPotential(test1));
        System.out.println("Vector outside the chamber: " + ih.eField.electricPotential(test2));
    }        

    public static void times2(Double d){
        d=d*2;
    }
}
