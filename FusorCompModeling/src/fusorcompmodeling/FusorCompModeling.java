/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;
import Jama.*;

/**
 *
 * @author guberti
 */
public class FusorCompModeling {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) {
        Point p1 = new Point(5, 0, 0);
        Point p2 = new Point(5, 0, 0);
        Point p3 = p1.crossProduct(p2);
        System.out.println(p3.toString());
        
    }

}
