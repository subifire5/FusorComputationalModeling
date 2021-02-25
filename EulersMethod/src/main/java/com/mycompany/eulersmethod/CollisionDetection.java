/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/**
 *
 * @author myan
 */
public class CollisionDetection {
    
    EField e;
    
    CollisionDetection(EField e) {
        this.e = e;
    }
    
    public boolean collisionCheck(Particle p, Particle p1) {
        Vector a = new Vector(p.pos);
        Vector b = new Vector((p1.pos.x-p.pos.x), (p1.pos.y-p.pos.y), (p1.pos.z-p.pos.z));
        //double lambda1 = (((this.pos.scale(-2.0)).dotProduct(this.vel)) + Math.sqrt(4 * (Math.pow(this.pos.dotProduct(this.vel), 2)) - (4 * (this.vel.dotProduct(this.vel)) * (this.pos.dotProduct(this.pos))))) / (2 * (this.vel).dotProduct(this.vel));
        //double lambda2 = (((this.pos.scale(-2.0)).dotProduct(this.vel)) - Math.sqrt(4 * (Math.pow(this.pos.dotProduct(this.vel), 2)) - (4 * (this.vel.dotProduct(this.vel)) * (this.pos.dotProduct(this.pos))))) / (2 * (this.vel).dotProduct(this.vel));
        double num1 = (4 * (Math.pow((a.dotProduct(b)),2))) - (4 * (b.dotProduct(b)) * (a.dotProduct(a)));
        return (4 * (Math.pow((a.dotProduct(b)),2))) - (4 * (b.dotProduct(b)) * (a.dotProduct(a))) >= 0.0;
    }
}
