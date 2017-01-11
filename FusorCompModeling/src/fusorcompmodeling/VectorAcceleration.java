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
public class VectorAcceleration extends Vector{
    public static double xAcceleration;
    public static double yAcceleration;
    public static double zAcceleration;
    public static void setXAcceleration(double XAcceleration){
        xAcceleration = XAcceleration;
    }
    public static void setYAcceleration(double YAcceleration){
        yAcceleration = YAcceleration;
    }
    public static void setZAcceleration(double ZAcceleration){
        zAcceleration = ZAcceleration;
    }
    public static double getXAcceleration(){
        return xAcceleration;
    }
    public static double getYAcceleration(){
        return yAcceleration;
    }
    public static double getZAcceleration(){
        return zAcceleration;
    }
}
