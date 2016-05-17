/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author guberti
 */
public class XMLParser {
    String path;
    public XMLParser(String path) {
        this.path = path;
    }
    
    public List<GridComponent> parseObjects() throws FileNotFoundException {
        List<GridComponent> parts = new ArrayList<>();

        try {

            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("shape");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nNode;
                        GridComponent e = parseElement(element);
                        parts.add(e);
                    }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File path is invalid!");
            throw e;
        } catch (Exception e) { 
            e.printStackTrace();
        }
        return parts;
    }
    
    GridComponent parseElement(Element element) {
        // All elements have x, y, z, phi, theta, radius, and type
        String type = element.getElementsByTagName("type").item(0).getTextContent();
        Vector v = new Vector();
        v.x = Double.parseDouble(element.getElementsByTagName("x").item(0).getTextContent());
        v.y = Double.parseDouble(element.getElementsByTagName("y").item(0).getTextContent());
        v.z = Double.parseDouble(element.getElementsByTagName("z").item(0).getTextContent());
        
        if(element.getElementsByTagName("phi").item(0).getTextContent().contains("pi")) {
            String temp = element.getElementsByTagName("phi").item(0).getTextContent();
            String[] containsPoint = temp.split("pi");
            v.phi = Double.parseDouble(containsPoint[1])*Math.PI;
        } else {
            v.phi = Double.parseDouble(element.getElementsByTagName("phi").item(0).getTextContent());
        }
        
        if (element.getElementsByTagName("theta").item(0).getTextContent().contains("pi")) {
            System.out.println("Theta contains pi!");
            String temp = element.getElementsByTagName("theta").item(0).getTextContent();
            String[] containsPoint = temp.split("pi");
            v.theta = Double.parseDouble(containsPoint[1])*Math.PI;
        } else {
            v.theta = Double.parseDouble(element.getElementsByTagName("theta").item(0).getTextContent());
        }
                
        double radius = Double.parseDouble(element.getElementsByTagName("radius").item(0).getTextContent());

        switch (type) {
            case "Cylinder":            
                double height = Double.parseDouble(element.getElementsByTagName("height").item(0).getTextContent());
                int charge = Integer.parseInt(element.getElementsByTagName("charge").item(0).getTextContent());
                System.out.println("Parsed a Cylinder with theta " + v.theta);
                return new Cylinder(v, radius, height, charge);
            case "TorusSegment":
                double radius2 = Double.parseDouble(element.getElementsByTagName("radius2").item(0).getTextContent());
                double phi2 = Double.parseDouble(element.getElementsByTagName("phi2").item(0).getTextContent());
                int charge2 = Integer.parseInt(element.getElementsByTagName("charge").item(0).getTextContent());
                System.out.println("Parsed a Torus Segment!");
                return new TorusSegment(v, radius, phi2, radius2, charge2);
            default:
                System.out.println("Unknown type " + type);
                return null;
        }
    }
}
