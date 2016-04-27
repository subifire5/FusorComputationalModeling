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
        int[] logNums = new int[]{10, 50, 100, 500, 1000, 5000, 10000, 50000};
        long[] times = new long[logNums.length];
        System.out.println("Loading file...");
        XMLParser p = new XMLParser("testXML.xml");
        List<GridComponent> parts = p.parseObjects();
        System.out.println("File loaded and initialized");
        Random rand = new Random();
        //-1 IS AN ANODE +, 1 IS A CATHODE -, 0 WILL BE NEUTRAL
        for(int i = 0; i < logNums.length; i++){
            long startTime = System.currentTimeMillis();
            ArrayList<Point> points = distributePoints(parts, logNums[i]);
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
            logFile = new BufferedWriter(new FileWriter("C:\\Users\\sfreisem-kirov\\Documents\\GitHub\\FusorComputationalModeling\\FusorCompModeling\\FusorLog.csv"));
            for(int i = 0; i < logNums.length; i++){
                logFile.write(""+ logNums[i] + "," + times[i]);
                logFile.newLine();
                logFile.flush();
            }
            logFile.close();
        } catch (Exception e) {
            System.err.println("Cannot create log file");

        }

    }

    public static ArrayList<Point> distributePoints(List<GridComponent> parts, int pointsForEachCharge) {
        ArrayList<Point> totalPoints = new ArrayList<Point>();
        Random newRand = new Random();
        for (int i = 0; i < pointsForEachCharge; i++) {
            totalPoints.add(getRandomPoint(parts, 1));
            totalPoints.add(getRandomPoint(parts, -1));
        }
        return totalPoints;
    }
    
    public static Point getRandomPoint(List<GridComponent> parts, int charge) {
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

    public static double electricPotential(ArrayList<Point> points, Point comparePoint) {
        double positivePotential = 0;
        double negativePotential = 0;
        for (int i = 0; i < points.size(); i++) {
            if (!points.get(i).equals(comparePoint)) {
                if (points.get(i).charge == 1) {
                    positivePotential += (1 / distanceCalculator(points.get(i), comparePoint));
                } else {
                    negativePotential += (1 / distanceCalculator(points.get(i), comparePoint));
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

    public static int balanceCharges(ArrayList<Point> points, List<GridComponent> parts) {
        /*Random newRand = new Random();
        int change = 0;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == -1) {
                int points = positivePoints.size();
                for (int j = 0; j < points; j++) {
                    Point comparePoint = parts.get(i).getRandomPoint(newRand);
                    double potentialOfComparePoint = electricPotential(positivePoints, negativePoints, comparePoint);
                    double potentialOfExistingPoint = electricPotential(positivePoints, negativePoints, positivePoints.get(j));
                    if (potentialOfComparePoint > potentialOfExistingPoint) {
                        positivePoints.set(j, comparePoint);
                        change++;
                    }
                }
            } else if (parts.get(i).charge == 1) {
                int points = negativePoints.size();
                for (int j = 0; j < points; j++) {
                    Point comparePoint = parts.get(i).getRandomPoint(newRand);
                    double potentialOfComparePoint = electricPotential(positivePoints, negativePoints, comparePoint);
                    double potentialOfExistingPoint = electricPotential(positivePoints, negativePoints, negativePoints.get(j));
                    if (potentialOfComparePoint > potentialOfExistingPoint) {
                        negativePoints.set(j, comparePoint);
                        change++;
                    }
                }
            }

        }
        return change;*/
        int changes = 0;
        
        for (int i = 0; i < points.size(); i++) {
            Point newPoint = getRandomPoint(parts, points.get(i).charge);
            double currentEP = electricPotential(points, points.get(i));
            double newEP = electricPotential(points, newPoint);
            if (newEP > currentEP) {
                changes++;
                points.set(i, newPoint);
            }
        }
        return changes;
    }
}
