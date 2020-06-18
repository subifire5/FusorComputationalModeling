/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

/**
 *
 * @author gmein
 */
public class Fusor {
    Assembly grounded; // chamber, cart, etc.
    Assembly negative; // grid
    Assembly floating; // everything not grounded: shielding, floor, etc.
    
    public Fusor(Assembly g, Assembly n, Assembly f) {
        this.grounded = g;
        this.negative = n;
        this.floating = f;
    }
}
