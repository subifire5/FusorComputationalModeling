/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
        int[] logNums = new int[]{100, 1000, 5000, 10000, 20000, 50000};
        long[] times = new long[logNums.length];
        System.out.println("Loading file...");
        XMLParser p = new XMLParser("simpleXML.xml");
        List<GridComponent> parts = p.parseObjects();
        System.out.println("File loaded and initialized");
        Random rand = new Random();
        //-1 IS AN ANODE +, 1 IS A CATHODE -, 0 WILL BE NEUTRAL
        for(int i = 0; i < logNums.length; i++){
            long startTime = System.currentTimeMillis();
            Point[] points = distributePoints(parts, logNums[i]);
            //int changes = 80;
            //int timesRun = 0;
            //do  {
            int changes = balanceCharges(points, parts);
            System.out.println("Changes Made: " + changes);
            long endTime = System.currentTimeMillis();
            times[i] = endTime - startTime;
        }    
        //timesRun++;
        //} while(changes > 0);
        //System.out.println("Times run: " + timesRun);
        BufferedWriter logFile = null;
        try {
            logFile = new BufferedWriter(new FileWriter("C:\\Users\\Daman\\Documents\\NetBeansProjects\\FusorComputationalModeling\\FusorCompModeling\\FusorLog.csv"));
            for(int i = 0; i < logNums.length ; i++){
                logFile.write("" + times[i]);
                logFile.newLine();
                logFile.flush();
            }
            logFile.close();
        } catch (Exception e) {
            System.err.println("Cannot create log file");

        }

    }

    public static Point[] distributePoints(List<GridComponent> parts, int pointsForEachCharge) {
        Point[] totalPoints = new Point[pointsForEachCharge];
        Random newRand = new Random();
        for (int i = 0; i < pointsForEachCharge; i++) {
            totalPoints[i] = getRandomPoint(parts);
        }
        return totalPoints;
    }
    
    public static Point getRandomPoint(List<GridComponent> parts) {
        int charge = 1;
        double area = totalSurfaceArea(parts, charge);
        Random generator = new Random();
        double rand = generator.nextDouble() * area;
        for (int i = 0; i < parts.size(); i++) {
            rand -= parts.get(i).getSurfaceArea();
            
            if (rand <= (double) 0.0) {
                return parts.get(i).getRandomPoint(new Random());
            }
        }
        return null; // Code will never reach here, but this line is required
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

    public static double electricPotential(Point[] points, Point comparePoint) {
        double positivePotential = 0;
        double negativePotential = 0;
        for (int i = 0; i < points.length; i++) {
            if (!points[i].equals(comparePoint)) {
                if (points[i].charge == 1) {
                    positivePotential += (1 / distanceCalculator(points[i], comparePoint));
                } else {
                    negativePotential += (1 / distanceCalculator(points[i], comparePoint));
                }
            }
        }
        return (positivePotential - negativePotential);
    }

    public static double distanceCalculator(Point a, Point b) {
        //This will make our calculations a lot more accurate because there are less floating point calculations as opposed to Math.pow()
        double distance = Math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.x - b.y) * (a.x - b.y)) + ((a.z - b.z) * (a.z - b.z)));
        return distance;
    }

    public static int balanceCharges(Point[] points, List<GridComponent> parts) {
        int changes = 0;
        
        for (int i = 0; i < points.length; i++) {
            Point newPoint = getRandomPoint(parts);
            double currentEP;
            if(points[i].EP == 0.0d) {
            currentEP = electricPotential(points, points[i]);
            } else {
                currentEP = points[i].EP;
            }
            double newEP = electricPotential(points, newPoint);
            if (newEP > currentEP) {
                changes++;
                points[i] = newPoint;
            }
            else if(newEP < currentEP) {
                points[i].EP = currentEP;
            }
        }
        return changes;
    }
}
