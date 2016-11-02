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
    
    // Units are degrees
    public double phi;
    public double theta;
    
    public Vector() {}
    
    public Vector(Point pos, double phi, double theta) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.phi = phi;
        this.theta = theta;
    }
    public Vector(double x, double y, double z, double phi, double theta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.theta = theta;
    }

    public Vector(double phi, double theta) {
        this.phi = phi;
        this.theta = theta;
    }
    
    public Point3D getAs3DPoint() {
        return new Point3D(x, y, z);
    }
    public Point convertRayToCartesian(double radius) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        return new Point(x, y, z);
    }
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.phi + ", " + this.theta + "]";
    }
}
