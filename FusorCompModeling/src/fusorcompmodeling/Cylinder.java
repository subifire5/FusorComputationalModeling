/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.*;

/**
 *
 * @author guberti
 */
public class Cylinder extends GridComponent {

    double height;

    public Cylinder(Vector pos, double radius, double height, int charge) {
        this.pos = pos;
        this.radius = radius;
        this.height = height;
        this.charge = charge;
    }

    public double getSurfaceArea() {
        return (Math.PI * radius * 2 * height);
    }

    public Point getRandomPoint(Random rand) {
        double pointPhi = rand.nextDouble() * Math.PI * 2;
        
        Point p = new Point();
        p.z = rand.nextDouble() * height;
        p.x = radius * Math.cos(pointPhi);
        p.y = radius * Math.sin(pointPhi);
        
        return p.rotateAroundVector(pos);
    }
    
    public Cylinder() {
    }
}
