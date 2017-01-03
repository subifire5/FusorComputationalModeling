/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import javafx.geometry.Point3D;
import Jama.*;

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
        double x = radius * Math.sin(theta) * Math.cos(phi);
        double z = radius * Math.sin(theta) * Math.sin(phi);
        double y = radius * Math.cos(theta);
        return new Point(x, y, z);
    }
    public Matrix convertRayToMatrix() {
        Point p = convertRayToCartesian(1);
        double[][] m = {{p.x}, {p.y}, {p.z}};
        return new Matrix(m);
    }
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.phi + ", " + this.theta + "]";
    }
    public Point rotatePoint(double radians, Matrix v) {
        // To perform vector rotations around other vectors, we must create three rotation vectors
        // For this, we can assume that both vectors are at the origin
        
        // This vector rotates the ray to be rotated around the plane that contains
        // the Y and Z axes
        double[][] arrMz = {
            {Math.cos(phi), -Math.sin(phi), 0}, 
            {Math.sin(phi), Math.cos(phi), 0}, 
            {0, 0, 1}};
        
        // This vector rotates the ray to be rotated around the Z axis
        double[][] arrMy = {
            {Math.cos(theta), 0, -Math.sin(theta)},
            {0, 1, 0},
            {Math.sin(theta), 0, Math.cos(theta)}};
        
        // This vector does the actual rotation
        double[][] arrR = {
            {Math.cos(radians), Math.sin(radians), 0},
            {-Math.sin(radians), Math.cos(radians), 0},
            {0, 0, 1}
        };
        
        // Then, we need to undo the first two rotations (in opposite order)
        
        // Convert our arrays into matrices
        Matrix Mz = new Matrix(arrMz);
        Matrix My = new Matrix(arrMy);
        Matrix R = new Matrix(arrR);
       
        // Apply the transormations
        Matrix vR = v.transpose();
        vR.times(Mz);
        vR.times(My);
        vR.times(R);
        vR.times(My.inverse()); // Invert the first two
        vR.times(Mz.inverse());
        double[][] arr = v.transpose().getArray(); // Change from matrix back to 2D array
        
        return new Point(arr[0][0], arr[0][1], arr[0][2]);
    }
    public Vector rotateVector(double radians, Matrix v) {
        Point p = rotatePoint(radians, v);
        // Convert back to spherical coords
        // We don't care about the radius, so we don't calculate it
        
        theta = Math.atan((
                Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.z, 2)))/
                p.y);
        
        phi = Math.atan(p.z/p.x);
        
        return new Vector(phi, theta);
    }
}
