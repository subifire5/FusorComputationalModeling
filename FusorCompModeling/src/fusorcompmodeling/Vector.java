/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import javafx.geometry.Point3D;

/**
 *
 * @author guberti
 */
public class Vector {
    // Units are mm
    public double x;
    public double y;
    public double z;
    public double length;
    // Units are degrees
    public double phi;
    public double theta;
    
    public Vector() {}
    
    public Vector(double x, double y, double z, double phi, double theta, double length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.theta = theta;
        this.length = length;
    }
    public Point3D getAs3DPoint() {
        return new Point3D(x, y, z);
    }
    public static double getLength(Point a, Point b){
        return Math.sqrt(((b.x-a.x)*(b.x-a.x)) + ((b.y-a.y)*(b.y-a.y))+((b.z-a.z)*(b.z-a.z)));
    }
    public static Vector Difference(Point a, Point b){
        Vector diff = new Vector();
        diff.x = (b.x-a.x);
        diff.y = (b.y-a.y);
        diff.z = (b.z-a.z);
        return diff;
    }
}
