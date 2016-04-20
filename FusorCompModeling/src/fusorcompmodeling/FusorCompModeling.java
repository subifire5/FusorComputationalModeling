/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
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
//        System.out.println("Obtaining a point...");
//        Point point = parts.get(1).getRandomPoint(rand);
//        System.out.println("Got a point");
//        System.out.println("[" + point.x + ", " + point.y + ", " + point.z + "]");
//        System.out.println(Math.pow(3 - Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2)), 2) + Math.pow(point.z, 2));
//        System.out.println(Math.pow(point.x, 2) + Math.pow(point.y, 2));
//
//        System.out.println("Initializing rotation tests...");
//        System.out.println("Rotating [2, 3, 5] around [0, 0, 0] by 0 and 0 radians");
//        Point p1 = new Point(2, 3, 5);
//        Vector v = new Vector(0, 0, 0, Math.PI, 0);
//        System.out.println(p1.rotateAroundVector(v).toString());
        //-1 IS AN ANODE +, 1 IS A CATHODE -, 0 WILL BE NEUTRAL
        ArrayList<Point> negativePoints = distributePoints(parts, 10, -1);
        ArrayList<Point> positivePoints = distributePoints(parts, 10, 1);
        System.out.println("Positive Points Size: " + positivePoints.size() + "\nNegative Points Size: " + negativePoints.size());
        //System.out.println(listOfAllPoints.size());
        //for (int i = 0; i < listOfAllPoints.size(); i++) {
        //System.out.println(listOfAllPoints.get(i).toString());
        //}
//        Point testPoint = new Point(rand.nextInt(100), rand.nextInt(100), rand.nextInt(100));
//        double totalPotential = electricPotential(positivePoints, negativePoints, testPoint);
//        System.out.println("Potential: " + totalPotential);
        int changes = balanceCharges(positivePoints, negativePoints, parts);
        System.out.println("Changes Made: " + changes);
    }

    public static ArrayList<Point> distributePoints(List<GridComponent> parts, int numberOfPoints, int charge) {
        System.out.println(numberOfPoints);
        ArrayList<Point> totalPointsOnAllShapes = new ArrayList<Point>();
        double totalSurfaceArea = totalSurfaceArea(parts, charge);
        Random newRand = new Random();
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == charge) {
                double percentage = (parts.get(i).getSurfaceArea() / totalSurfaceArea);
                double timesToRun = Math.floor(numberOfPoints * percentage);
                for (int j = 0; j < timesToRun; j++) {
                    totalPointsOnAllShapes.add(parts.get(i).getRandomPoint(newRand));
                }
            }
        }
        System.out.println(totalSurfaceArea);
        return totalPointsOnAllShapes;
    }

    public static double totalSurfaceArea(List<GridComponent> parts, int charge) {
        double surfaceArea = 0;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == charge) {
                surfaceArea += parts.get(i).getSurfaceArea();
            }
        }
        return surfaceArea;
    }

    public static double electricPotential(ArrayList<Point> positivePoints, ArrayList<Point> negativePoints, Point comparePoint) {
        double positivePotential = 0;
        double negativePotential = 0;
        for (int i = 0; i < positivePoints.size(); i++) {
            if (!positivePoints.get(i).equals(comparePoint)) {
                positivePotential += (1 / distanceCalculator(positivePoints.get(i), comparePoint));

            }
        }
        for (int i = 0; i < negativePoints.size(); i++) {
            if (!negativePoints.get(i).equals(comparePoint)) {
                negativePotential += (1 / distanceCalculator(negativePoints.get(i), comparePoint));
            }
        }
        return (positivePotential - negativePotential);
    }

    public static double distanceCalculator(Point a, Point b) {
        double distance = 0;
        distance = Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2) + Math.pow((a.z - b.z), 2));
        return distance;
    }

    public static int balanceCharges(ArrayList<Point> positivePoints, ArrayList<Point> negativePoints, List<GridComponent> parts) {
        Random newRand = new Random();
        int change = 0;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == -1) {
                for (int j = 0; j < positivePoints.size(); j++) {
                    Point comparePoint = parts.get(i).getRandomPoint(newRand);
                    double potentialOfComparePoint = electricPotential(positivePoints, negativePoints, comparePoint);
                    double potentialOfExistingPoint;
                    potentialOfExistingPoint = electricPotential(positivePoints, negativePoints, positivePoints.get(j));
                    if (potentialOfComparePoint > potentialOfExistingPoint) {
                        positivePoints.add(j, comparePoint);
                        change++;
                    }
                }
            } else if (parts.get(i).charge == 1) {
                for (int j = 0; j < negativePoints.size(); j++) {
                    Point comparePoint = parts.get(i).getRandomPoint(newRand);
                    double potentialOfComparePoint = electricPotential(positivePoints, negativePoints, comparePoint);
                    double potentialOfExistingPoint;
                    potentialOfExistingPoint = electricPotential(positivePoints, negativePoints, negativePoints.get(j));
                    if (potentialOfComparePoint > potentialOfExistingPoint) {
                        negativePoints.add(j, comparePoint);
                        change++;
                    }
                }
            }

        }
        return change;
    }
}
