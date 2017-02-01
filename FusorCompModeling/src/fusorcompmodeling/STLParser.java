package fusorcompmodeling;

import fusorcompmodeling.Cylinder;
import fusorcompmodeling.Sphere;
import fusorcompmodeling.TorusSegment;
import fusorcompmodeling.Triangle;
import fusorcompmodeling.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import matheval.MathEval;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.j3d.loaders.stl.STLFileReader;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ethan
 */
public class STLParser {

    String path;
    MathEval eval;
    int[] test;
    List<Triangle> tList = new ArrayList<Triangle>();

    public STLParser(String path) {
        this.path = path;
        eval = new MathEval();
        eval.setConstant("Pi", Math.PI);
    }

    public List<Triangle> parseObjects() throws FileNotFoundException, IOException {
        List<Triangle> parts = new ArrayList<>();
        File txtfile = new File("block.txt");

        try {
            
            double[] normal = new double[3];
            double[][] vertices = new double[3][3];
            STLFileReader stlFR = new STLFileReader("block.stl");
            stlFR.getNextFacet(normal, vertices);
            
            /* 
            Scanner scanner = new Scanner(txtfile);
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if (line.matches(".*\\bvertex\\b.*")) {
                    //Triangle t;
                    for (int i = 0; i < 3; i++) {
                        Point[] pArray = new Point[3];
                        for (int j = 0; j < 3; j++) {

                            String[] coordinate = new String[3];
                            
                            line = line.replace("vertex", "").trim();
                            coordinate = line.split(" ");
                            for (int h = 0; h < 3; h++) {
                                coordinate[h] = coordinate[h].substring(0, coordinate[h].length() - 5);

                            }
                            Point p = new Point(Double.parseDouble(coordinate[0]), Double.parseDouble(coordinate[1]), Double.parseDouble(coordinate[2]), 0, 0);
                            pArray[j] = p;
                            lineNum++;
                        }
                        Triangle t = new Triangle(pArray, 0);
                        parts.add(t);
                        
                    }
                } 
            }*/
        } catch (FileNotFoundException e) {
            //handle this
        }

        /*try {
            File file = new File(path);
            STLFileReader stlReader = new STLFileReader(file);
            test = stlReader.getNumOfFacets();
            System.out.println("Facet Number: " + test[0]);
            

        } catch (FileNotFoundException e) {
            System.out.println("File path is invalid!");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } */
        return parts;
    }

    Triangle parseElement(Element element) {

        return null;
    }

    public double parseDouble(Element element, String tag) {
        String textContent = element.getElementsByTagName(tag).item(0).getTextContent();
        return eval.evaluate(textContent);
    }

    public int parseCharge(Element element) {
        return Integer.parseInt(element.getElementsByTagName("charge").item(0).getTextContent());
    }
}
