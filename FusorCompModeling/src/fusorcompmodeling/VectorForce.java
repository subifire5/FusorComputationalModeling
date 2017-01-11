/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

/**
 *
 * @author sfreisem-kirov
 */
public class VectorForce extends Vector{
    public static double xForce;
    public static double yForce;
    public static double zForce;
    public static void setXForce(double XForce){
        xForce = XForce;
    }
    public static void setYForce(double YForce){
        yForce = YForce;
    }
    public static void setZForce(double ZForce){
        zForce = ZForce;
    }
    public static double getXForce(){
        return xForce;
    }
    public static double getYForce(){
        return yForce;
    }
    public static double getZForce(){
        return zForce;
    }
}
