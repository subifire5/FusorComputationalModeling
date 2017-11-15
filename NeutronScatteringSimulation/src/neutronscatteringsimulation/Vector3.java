/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

/**
 *
 * @author jfellows
 */
public class Vector3 {

    double x;
    double y;
    double z;
    int i;

    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double distance(Vector3 v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2) + Math.pow(z - v.z, 2));
    }

    Vector3 subtract(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    Vector3 add(Vector3 p) {
        return new Vector3(x + p.x, y + p.y, z + p.z);
    }
    
    Vector3 add(double s) {
        return new Vector3(x + s, y + s, z + s);
    }

    Vector3 multiply(double s) {
        return new Vector3(x * s, y * s, z * s);
    }
    
    Vector3 multiply(Vector3 v) {
        return new Vector3(x * v.x, y * v.y, z * v.z);
    }

    Vector3 divide(double s) {
        return new Vector3(x / s, y / s, z / s);
    }

    double length() {
        return distance(new Vector3(0, 0, 0));
    }
    
    double dotProduct(Vector3 v) {
        return (x * v.x) + (y * v.y) + (z * v.z);
    }
    
    Vector3 crossProduct(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    Vector3 normalize() {
        return divide(length());
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
