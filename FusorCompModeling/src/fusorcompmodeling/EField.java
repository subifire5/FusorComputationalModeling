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
    public EField() {
        
    }
    public static double kQ;
    
    public static void setkQ(double voltageAnnode, double voltageCathode, Point[] points) {
        double DeltaPhi = getDeltaPhi(avgPotential(points, 1), avgPotential(points, -1));
        double KQ;
        double DeltaVoltage = voltageAnnode - voltageCathode;
        KQ = (DeltaVoltage / DeltaPhi) * 0.001;//m/mm  
        kQ=KQ;
    }
    
    public static Ray EFieldSum(Point[] points, Point s){
        Ray e = new Ray();
        Ray eSum = new Ray();
        eSum.x= 0;
        eSum.y=0;
        eSum.z=0;
        
                
        Ray r;
        double rLen;
        
        for(int i = 0; i < points.length; i++){
            r = Ray.Difference(s,points[i]);
            rLen= Ray.getLength(s, points[i]);

            e.x = r.x/(rLen*rLen*rLen);
            e.y = r.y/(rLen*rLen*rLen);
            e.z = r.z/(rLen*rLen*rLen);

            e.x *=kQ * points[i].charge;
            e.y *=kQ * points[i].charge;
            e.z *=kQ * points[i].charge;
            
            eSum.x+= e.x;
            eSum.y+=e.y;
            eSum.z+=e.z;
            
        }
        return eSum;
    }
}
