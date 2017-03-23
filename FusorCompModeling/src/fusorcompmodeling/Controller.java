/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sfreisem-kirov
 */
public class Controller {
    EField e = new EField();
    Solver s = new euler();  
    static ArrayList<Atom> Atoms = new ArrayList();
    
    public Controller(Point[] points, double voltageAnnode, double voltageCathode){
        e.setkQ(voltageAnnode,voltageCathode,points);
    }
    
    public static void stepAllForeward(int numPSecs){
        for(int i = 0; i < Atoms.size(); i++){
            
        }
    }
    public static void addAtom(Point pos, double mass){
        Vector v = new Vector();
        v.x=0;
        v.y=0;
        v.z=0;
        Atom a = new Atom();
        a.position = pos;
        a.Velocity = v;
        a.mass = mass;
    }
}
