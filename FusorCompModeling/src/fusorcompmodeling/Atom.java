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
public class Atom {
    public static Point position;
    public static VectorVelocity Velocity;
    public static VectorAcceleration Acceleration;
    public static void setPos(Point Position){
        position = Position;
    }
    public static Point getPos(){
        return position;
    }
    public static void setVelocity(VectorVelocity Vel){
        Velocity = Vel;
    }
    public static VectorVelocity getVelocity(){
        return Velocity;
    }
    public static void setAcceleration(VectorAcceleration Acc){
        Acceleration = Acc;
    }
    public static VectorAcceleration getAcceleration(){
        return Acceleration;
    }
}
