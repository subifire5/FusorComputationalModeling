/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.Random;

/**
 *
 * @author guberti
 */
public class TorusSegment extends GridComponent {
    double phi2;
    double phi3;
    double radius2;
    Vector startPos;
    
    public TorusSegment (Vector pos, double radius, double phi2, double phi3, double radius2, int charge) {
        this.pos = pos;
        this.radius = radius;
        this.phi2 = phi2;
        this.phi3 = phi3;
        this.radius2 = radius2; // Width of the torus
        this.charge = charge;
        this.type = ComponentType.TorusSegment;
    }
    
    // When constructing torus segments as part of a wire, saving some of the
    // calculated data when the torus segment is placed in the vector
    // endPos is helpful in calculating the position of the next cylinder
    
    public TorusSegment (Vector pos, double radius, double phi2, double phi3, double radius2, int charge, Vector startPos) {
        this(pos, radius, phi2, phi3, radius2, charge);
        this.startPos = startPos;
        
    }
    
    @Override
    public double getSurfaceArea() {
        return (4 * Math.pow(Math.PI, 2) * radius * radius2);
    }
    
    @Override
    public Point getRandomPoint(Random r) {
        double pointPhi = phi2 + (phi3 - phi2) * r.nextDouble();
        double pointTheta = r.nextDouble() * Math.PI * 2;
        
        // Points without rotation
        Point p = new Point();
        
        p.y = pos.y + radius2 * Math.sin(pointTheta);
        
        double a = radius2 * Math.cos(pointTheta);
        p.x = pos.x + (radius + a) * Math.cos(pointPhi);
        p.z = pos.z + (radius + a) * Math.sin(pointPhi);
        p.charge = charge;
        
        return p.rotateAroundVector(pos);
    }
    
    @Override
    public String toString() {
        return "Torus with radii " + radius + ", " + radius2 +
                " and position [" + pos.x + ", " + pos.y + ", " + pos.z;
    }
}
