/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;
import org.fxyz3d.shapes.primitives.CuboidMesh;

public class CubeFXyz extends Shape {
    CubeFXyz(double w, double h, double d) {
        super(new CuboidMesh(w,h,d));
    }
}
