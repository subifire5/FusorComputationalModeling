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
    double radius2;
    public TorusSegment (Vector pos, double radius, double phi2, double radius2) {
        this.pos = pos;
        this.radius = radius;
        this.phi2 = phi2;
        this.radius2 = radius2; // Width of the torus
    }
    
    // TODO add a real method here 
    
    @Override
    public Point getRandomPoint(Random r) {
        double pointPhi = r.nextDouble() * Math.PI * 2; // Tau radians in a circle
        double pointTheta = r.nextDouble() * Math.PI * 2;
        
        // Points without rotation
        double pointZ = pos.z + radius2 * Math.sin(pointTheta);
        
        double a = radius2 * Math.cos(pointTheta);
        double pointX = pos.x + (radius + a) * Math.cos(pointPhi);
        double pointY = pos.y + (radius + a) * Math.sin(pointPhi);
                
        return new Point(pointX, pointY, pointZ);
    }
}
