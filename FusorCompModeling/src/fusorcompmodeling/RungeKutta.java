package fusorcompmodeling;

import fusorcompmodeling.Atom;
import fusorcompmodeling.EField;
import fusorcompmodeling.Point;
import fusorcompmodeling.Solver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author daman
 */
public class RungeKutta implements Solver {
    
    EField e;
    
    public RungeKutta() {
        this.e = null;
    }
    
    public void setEField(EField e) {
        this.e = e;
    }

    @Override
    public Atom moveForward(Point[] points, double timeStep, Atom a, EField e) {
        double[] y0 = {a.position.x, a.position.y, a.position.z, a.Velocity.x, a.Velocity.y, a.Velocity.z};
        double[] k1 = f(y0, a.mass);
        double[] y1 = helper(timeStep, k1, y0, 0.5);
        double[] k2 = f(y1, a.mass);
        double[] y2 = helper(timeStep, k2, y0, 0.5);
        double[] k3 = f(y2, a.mass);
        double[] y3 = helper(timeStep, k3, y0, 1);
        double[] k4 = finalAverage(k1, k2, k3, y0, timeStep);
 
        a.position.x = k4[0];
        a.position.y = k4[1];
        a.position.z = k4[2];
        
        a.Velocity.x = k4[3];
        a.Velocity.y = k4[4];
        a.Velocity.z = k4[5];
        
        return a;
    }
    
    public double[] finalAverage(double[] k1, double[] k2, double[] k3, double[] y0, double timeStep) {
        for(int i = 0; i < k2.length; i++) {
            k2[i] *= 2;
            k3[i] *= 2;
        }
        double[] toReturn = new double[6];
        for(int i = 0; i < k2.length; i++) {
            toReturn[i] = k1[i] + k2[i] + k3[i];
            toReturn[i] *= (timeStep/6);
            toReturn[i] += y0[i];
        }
        return toReturn;
    }
    
    public double[] helper(double ts, double[] k, double[] y, double multiplier) {
        double kmult = ts*multiplier;
        double[] newY = new double[6];
        for(int i = 0; i < k.length; i++) {
            k[i] = k[i] * kmult;
            newY[i] = y[i] + k[i];
        }
        return newY;
    }
    
    public double[] f (double[] y, double mass) {
        double[] VelAcc = {y[3], y[4], y[5], 0, 0, 0};
        Point p = new Point(y[0], y[1], y[2]);
        Vector acc = e.FToAcc(p, mass);
        VelAcc[3] = acc.x;
        VelAcc[4] = acc.y;
        VelAcc[5] = acc.z;
        return VelAcc;
    }
    

}
