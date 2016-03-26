/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

/**
 *
 * @author guberti
 */
public class FusorCompModeling {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Loading file...");
        XMLParser p = new XMLParser("testXML.xml");
        List<GridComponent> parts = p.parseObjects();
        System.out.println("File loaded and initialized");
        Random rand = new Random();
        System.out.println("Obtaining a point...");
        Point point = parts.get(1).getRandomPoint(rand);
        System.out.println("Got a point");
        System.out.println("[" + point.x + ", " + point.y + ", " + point.z + "]");
        //System.out.println(Math.pow(3 - Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2)), 2) + Math.pow(point.z, 2));
        System.out.println(Math.pow(point.x, 2) + Math.pow(point.y, 2));
        
        System.out.println("Initializing rotation tests...");
        System.out.println("Rotating [2, 3, 5] around [0, 0, 0] by 0 and 0 radians");
        Point p1 = new Point(2, 3, 5);
        Vector v = new Vector(0, 0, 0, Math.PI, 0);
        System.out.println(p1.rotateAroundVector(v).toString());
    }
    
}
