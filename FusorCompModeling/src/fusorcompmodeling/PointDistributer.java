/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author guberti
 */
public class PointDistributer {
    
    public PointDistributer () {}
    
    public static ArrayList<Point> shakeUpPoints(List<GridComponent> parts, int pointsPerCharge, int endCon) {
        ArrayList<Point> points = distributePoints(parts, pointsPerCharge);
        int timesSinceLastChange = 0;
        while (timesSinceLastChange < endCon) {
            int changesMade = balanceCharges(points, parts);
            System.out.println("Shook up the points, " + changesMade + " changes made");
            if (changesMade == 0) {
                timesSinceLastChange++;
            }
        }
        return points;
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

    ArrayList<Point> shakeUpPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
