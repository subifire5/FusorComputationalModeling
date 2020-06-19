/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

public class Vacuum extends Air {

    Vacuum() {
        // Vacuum is just air at really low pressure
        // 1 micron = 0.0001 kPa
        super("Vacuum", 1e-4);
    }

}
