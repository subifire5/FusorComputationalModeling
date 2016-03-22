/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

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
        this.radius2 = radius2;
    }
    
    public TorusSegment () {}
}
