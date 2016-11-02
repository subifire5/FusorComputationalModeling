/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.Comparator;

/**
 *
 * @author guberti
 */
public class Point {
    public double x;
    public double y;
    public double z;
    public int charge; // True is positive, false is negative
    public double EP;
    
    public Point(double x, double y, double z, int charge, double EP) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.charge = charge;
        this.EP = EP;
    }
    
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

        rP.z = Math.cos(-v.phi) * mP.x + Math.sin(-v.phi) * Math.sin(-v.theta) * mP.y -
                Math.sin(-v.phi) * Math.cos(-v.theta) * mP.z;
        
        rP.y = Math.cos(-v.theta) * mP.y + Math.sin(-v.theta) * mP.z;
        
        rP.x = Math.sin(-v.phi) * mP.x + Math.cos(-v.phi) * -1 * Math.sin(-v.theta) * 
                mP.y + Math.cos(-v.phi) * Math.cos(-v.theta) * mP.z;
        
        // Then add the coordinates the point is being rotated around
        
        rP.x += v.x;
        rP.y += v.y;
        rP.z += v.z;
        rP.charge = charge;
        // Return the rotated point
        
        return rP;
    }
    
    public Point crossProduct(Point p) {
        Point nP = new Point();
        
        nP.x = y*p.z - z*p.y;
        nP.y = z*p.x - x*p.z;
        nP.z = x*p.y - y*p.x;
        
        return nP;
    }
    
/*    public static final Comparator<Point> X_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.x < o2.x) {
                return -1;
            }
            if (o1.x > o2.x) {
                return 1;
            }
            return 0;
        }
    };

    public static final Comparator<Point> Y_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.y < o2.y) {
                return -1;
            }
            if (o1.y > o2.y) {
                return 1;
            }
            return 0;
        }
    };

    public static final Comparator<Point> Z_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.z < o2.z) {
                return -1;
            }
            if (o1.z > o2.z) {
                return 1;
            }
            return 0;
        }
    };*/

    public int compareTo(Point b, int axis) {
        if (axis == 0) {
            if (this.x > b.x) {
                return -1;
            } else if (this.x < b.x) {
                return 1;
            }
        } else if (axis == 1) {
            if (this.y > b.y) {
                return -1;
            } else if (this.y < b.y) {
                return 1;
            }
        } else if (axis == 2) {
            if (this.z > b.z) {
                return -1;
            } else if (this.z < b.z) {
                return 1;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "[" + Double.toString(x) + 
                ", " + Double.toString(y) + ", " + 
                Double.toString(z) + "]";
    }

    void divideByLength(double len) {
        x /= len;
        y /= len;
        z /= len;
    }
    
    double getLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }
}
