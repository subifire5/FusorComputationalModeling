/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Physics {

    final static double boltzmann = 8.61733333353e-5; //eV/K
    final static double roomTemp = 293.0; //K
    final static double protonMass = 1.007276; // amu

    public static Vector3D randomDir() {
        double theta = (Util.random.nextDouble() * 2 * Math.PI);
        double phi = Math.acos(Util.random.nextDouble() * 2 * Math.PI - 1);
        return new Vector3D(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi));
    }

}
