/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import static fusorcompmodeling.PointDistributer.electricPotential;

/**
 *
 * @author sfreisem-kirov
 */
public class StatsGen {
    public StatsGen() {
    
    }
    public static double avgPotential(Point[] points, int charge){
        double avgPotential;
        double totalPotential = 0;
        for(int i = 0; i < points.length; i++){
            if (points[i].charge == charge){
                totalPotential += electricPotential(points,points[i]);
            }
        }
        avgPotential = totalPotential/points.length;
        return avgPotential;
        
    }
    public static Acceleration getAcceleration(Point[] points, Point point){
        double x;
        double y;
        double z;
        
        Acceleration acc = new Acceleration(x,y,z);
        
        return acc;
    }
    public static double getDeltaPhi(double posAvgPot, double negAvgPot){
        double DeltaPhi;
        DeltaPhi = posAvgPot - negAvgPot;
        return DeltaPhi;
    }
    public static double getkQ(Point[] points, double voltage){
        double DeltaPhi = getDeltaPhi(avgPotential(points,1),avgPotential(points,1));
        double kQ;
        kQ = (voltage/DeltaPhi)*0.01;//m/cm
        
        return kQ;
    }
    public static VectorForce getVforce(Point[] points, double voltage, )
}
