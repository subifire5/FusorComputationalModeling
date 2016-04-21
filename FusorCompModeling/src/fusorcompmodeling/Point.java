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
public class Point {
    public double x;
    public double y;
    public double z;
    public int charge; // True is positive, false is negative
    
    public Point(double x, double y, double z, int charge) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.charge = charge;
    }
    
    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point() {}
    
    public Point rotateAroundVector(Vector v) {

        // First move the two points so that the point can be rotated around
        // the origin
        Point mP = new Point(x - v.x, y - v.y, z - v.z);
        
        // Then rotate the point around the origin with the rotational formula
        Point rP = new Point(); // rP stands for rotatedPoint

        rP.x = Math.cos(v.phi) * mP.x + Math.sin(v.phi) * Math.sin(v.theta) * mP.y -
                Math.sin(v.phi) * Math.cos(v.theta) * mP.z;
        
        rP.y = Math.cos(v.theta) * mP.y + Math.sin(v.theta) * mP.z;
        
        rP.z = Math.sin(v.phi) * mP.x + Math.cos(v.phi) * -1 * Math.sin(v.theta) * 
                mP.y + Math.cos(v.phi) * Math.cos(v.theta) * mP.z;
        
        // Then add the coordinates the point is being rotated around
        
        rP.x += v.x;
        rP.y += v.y;
        rP.z += v.z;
        rP.charge = charge;
        // Return the rotated point
        
        return rP;
    }
    
    @Override
    public String toString() {
        return "[" + Double.toString(x) + 
                ", " + Double.toString(y) + ", " + 
                Double.toString(z) + "]";
    }
}
