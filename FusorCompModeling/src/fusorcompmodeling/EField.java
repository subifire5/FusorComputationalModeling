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
    public Point[] p;
    
    public void setPoints(Point[] p) {
        this.p = p;
    }
    
    public void setkQ(double voltageAnnode, double voltageCathode) {
        double DeltaPhi = getDeltaPhi(avgPotential(this.p, 1), avgPotential(this.p, -1));
        double KQ;
        double DeltaVoltage = voltageAnnode - voltageCathode;
        KQ = (DeltaVoltage / DeltaPhi) * 0.001;//m/mm  
        kQ=KQ;
    }
    
    public Vector EFieldSum(Point s){
        Vector e = new Vector();
        Vector eSum = new Vector();
        eSum.x= 0;
        eSum.y=0;
        eSum.z=0;
        
                
        Vector r;
        double rLen;
        
        for(int i = 0; i < this.p.length; i++){
            r = Vector.Difference(s,this.p[i]);
            rLen= Vector.getLength(s, this.p[i]);

            e.x = r.x/(rLen*rLen*rLen);
            e.y = r.y/(rLen*rLen*rLen);
            e.z = r.z/(rLen*rLen*rLen);

            e.x *=kQ * this.p[i].charge;
            e.y *=kQ * this.p[i].charge;
            e.z *=kQ * this.p[i].charge;
            
            eSum.x+= e.x;
            eSum.y+=e.y;
            eSum.z+=e.z;
            
        }
        return eSum;
    }
    
    public Vector FToAcc(Point r, double mass){
        Vector vAcc = this.EFieldSum(r);
        
        vAcc.x = vAcc.x/mass;
        vAcc.y = vAcc.y/mass;
        vAcc.z = vAcc.z/mass;
        
        return vAcc;
    }
}
