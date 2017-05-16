/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;

import fusorcompmodeling.Vector;

/**
 *
 * @author guberti
 */
public class EFieldData {
    Vector v;
    double d;
    public EFieldData(Vector v) {
        this.v = v;
    }
    public EFieldData(double d) {
        this.d = d;
    }
    public double getDouble() {
        return d;
    }
    public Vector getVector() {
        return v;
    }
}
