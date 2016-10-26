/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import static fusorcompmodeling.StatsGen.avgPotential;
import static fusorcompmodeling.StatsGen.getDeltaPhi;

/**
 *
 * @author sfreisem-kirov
 */
public class EField {
    
    public static double getkQ(double voltage, Point[] points) {
        double DeltaPhi = getDeltaPhi(avgPotential(points, 1), avgPotential(points, 1));
        double kQ;
        kQ = (voltage / DeltaPhi) * 0.01;//m/cm  
        return kQ;
    }
    public static Vector EFieldSum(double voltage, Point[] points, Vector r){
        return r;
    }
}
