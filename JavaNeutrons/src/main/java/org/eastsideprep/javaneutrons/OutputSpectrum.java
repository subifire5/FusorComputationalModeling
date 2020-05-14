/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.HashMap;

/**
 *
 * @author gunnar
 */
public class OutputSpectrum {
    STLShape window; // if null, output past all assemblies
    HashMap<Double, Integer> spectrum; // energy->count
}
