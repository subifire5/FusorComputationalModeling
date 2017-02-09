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
public class VectorVelocity extends Vector{
    public static double xVelocity;
    public static double yVelocity;
    public static double zVelocity;
    public static void setXVelocity(double XVelocity){
        xVelocity = XVelocity;
    }
    public static void setYVelocity(double YVelocity){
        yVelocity = YVelocity;
    }
    public static void setZVelocity(double ZVelocity){
        zVelocity = ZVelocity;
    }
    public static double getXVelocity(){
        return xVelocity;
    }
    public static double getYVelocity(){
        return yVelocity;
    }
    public static double getZVelocity(){
        return zVelocity;
    }
}
