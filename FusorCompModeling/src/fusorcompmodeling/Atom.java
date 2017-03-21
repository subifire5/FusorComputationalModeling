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
    public Atom(){
    
    }
    public static Point position;
    public static Vector Velocity;
    public static void setPos(Point Position){
        position = Position;
    }
    public static Point getPos(){
        return position;
    }
    public static void setVelocity(Vector Vel){
        Velocity = Vel;
    }
    public static Vector getVelocity(){
        return Velocity;
    }

}
