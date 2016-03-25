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
        XMLParser p = new XMLParser("/Users/guberti/Documents/GitHub/FusorComputationalModeling/FusorCompModeling/testXML.xml");
        List<GridComponent> parts = p.parseObjects();
        Random rand = new Random();
        Point point = parts.get(0).getRandomPoint(rand);
        System.out.println("[" + point.x + ", " + point.y + ", " + point.z + "]");
        System.out.println(Math.pow(3 - Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2)), 2) + Math.pow(point.z, 2));
    }
    
}
