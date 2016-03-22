/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 * @author guberti
 */
public class FusorCompModeling {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        XMLParser p = new XMLParser("/Users/guberti/Documents/GitHub/FusorComputationalModeling/FusorCompModeling/testXML.xml");
        List<GridComponent> parts = p.parseObjects();
    }
    
}
