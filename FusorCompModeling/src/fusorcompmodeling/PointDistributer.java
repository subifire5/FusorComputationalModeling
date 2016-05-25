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
    
    public static Point[] shakeUpPoints(List<GridComponent> parts, int pointsPerCharge, int endCon) {
        Point[] points = distributePoints(parts, pointsPerCharge);
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
    
    public static Point[] distributePoints(List<GridComponent> parts, int pointsForEachCharge) {
        Point[] totalPoints = new Point[pointsForEachCharge];
        Random newRand = new Random();
        for (int i = 0; i < pointsForEachCharge; i++) {
            totalPoints[i] = getRandomPoint(parts);
        }
        return totalPoints;
    }

    public static Point getRandomPoint(List<GridComponent> parts) {
        double area = totalSurfaceArea(parts);
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

    public static double totalSurfaceArea(List<GridComponent> parts) {
        double surfaceArea = 0;
        for (int i = 0; i < parts.size(); i++) {
            //System.out.println(parts.get(i).toString());
            surfaceArea += parts.get(i).getSurfaceArea();
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
        double distance = Math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y)) + ((a.z - b.z) * (a.z - b.z)));
        return distance;
    }

    public static int balanceCharges(Point[] points, List<GridComponent> parts) {
        int changes = 0;
        for (int i = 0; i < points.length; i++) {
            Point newPoint = getRandomPoint(parts);
            double currentEP = electricPotential(points, points[i]);
            double newEP = electricPotential(points, newPoint);
            if (newEP < currentEP) {
                changes++;
                points[i] = newPoint;
            } else if (newEP > currentEP) {
                points[i].EP = currentEP;
            }
        }
        return changes;
    }

    ArrayList<Point> shakeUpPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
