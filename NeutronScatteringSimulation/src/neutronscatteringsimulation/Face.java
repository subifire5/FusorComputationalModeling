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
public class Face {

    final int p1;
    final int p2;
    final int p3;

    Face(int p1, int p2, int p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
    
    Face reverse() {
        return new Face(p3, p2, p1);
    }
}
