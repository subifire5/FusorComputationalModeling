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
public class Controller {
    int length = 0;
    public Atom[] Atoms = new Atom[length];
    
    public Controller(int numAtoms){
        length = numAtoms;
    }
    public static void stepForeward(int numPSecs){
        
    }
}
