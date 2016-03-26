/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

/**
 *
 * @author guberti
 */
public class Vector {
    // Units are mm
    public double x;
    public double y;
    public double z;
    
    // Units are degrees
    public double phi;
    public double theta;
    
    public Vector() {}
    
    public Vector(double x, double y, double z, double phi, double theta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.theta = theta;
    }
}
