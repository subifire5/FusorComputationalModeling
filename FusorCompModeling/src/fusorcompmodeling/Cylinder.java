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
public class Cylinder extends GridComponent {
    double height;
    
    public Cylinder (Vector pos, double radius, double height) {
        this.pos = pos;
        this.radius = radius;
        this.height = height;
    }
    
    public Point getRandomPoint(Random r) {
        double pointPhi = r.nextDouble() * Math.PI * 2;
        double pointHeight = r.nextDouble() * height;
        
        return new Point(0, 0, 0);
    }
}
