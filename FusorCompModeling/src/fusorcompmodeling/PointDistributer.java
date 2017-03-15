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

    public PointDistributer() {
    }

    public static Point[] shakeUpPoints(List<GridComponent> parts, int pointsPerCharge, int endCon) {
        Point[] points = distributePoints(parts, pointsPerCharge);
        int reps = 0;
        while (reps < endCon) {
            int changesMade = balanceCharges(points, parts);
            reps++;
        }
        return points;
    }

    public static Point[] distributePoints(List<GridComponent> parts, int pointsForEachCharge) {
        Point[] totalPoints = new Point[pointsForEachCharge*2];
        Random newRand = new Random();
        for (int i = 0; i < pointsForEachCharge*2; i += 2) {
            totalPoints[i] = getRandomPoint(parts, -1);
            totalPoints[i + 1] = getRandomPoint(parts, 1);
        }
        return totalPoints;
    }

    public static Point getRandomPoint(List<GridComponent> parts, int charge) {
        
        double area = totalSurfaceArea(parts, charge);
        Random generator = new Random();

        double rand = generator.nextDouble() * area;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == charge) {
                rand -= parts.get(i).getSurfaceArea();

                if (rand <= (double) 0.0) {
                    return parts.get(i).getRandomPoint(new Random());
                }
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
        double potential = 0;
        for (int i = 0; i < points.length; i++) {
            if (!points[i].equals(comparePoint)) {

                potential += (points[i].charge * (1 / distanceCalculator(points[i], comparePoint)));

            }
        }
        return potential;
    }

    public static double distanceCalculator(Point a, Point b) {

        // I'm using the slower math.pow here, because it doesn't seem to make a big impact on performance
        // However, a more thourough investigation is required here
        double distSquared = (Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
        return Math.sqrt(distSquared);
    }

    public static int balanceCharges(Point[] points, List<GridComponent> parts) {
        int changes = 0;
        
        for (int i = 0; i < points.length; i++) {            
            Point newPoint = getRandomPoint(getReachableShapes(points[i].charge, parts), points[i].charge);
            double currentEP = electricPotential(points, points[i]);

            double newEP = electricPotential(points, newPoint);
            if (points[i].charge == 1) {
                if (newEP < currentEP) {
                    changes++;
                    points[i] = newPoint;
                } else if (newEP > currentEP) {
                    points[i].EP = currentEP;
                }

            } else if (newEP > currentEP) {
                changes++;
                points[i] = newPoint;
            } else if (newEP < currentEP) {
                points[i].EP = currentEP;
            }
        }
        return changes;
    }

    public static List<GridComponent> getReachableShapes(int charge, List<GridComponent> parts) {
        List<GridComponent> ReachableShapes = new ArrayList<>();
        int i = 0;
        while (i < parts.size()) {
            if (charge == parts.get(i).charge) {
                ReachableShapes.add(parts.get(i));
            }
            i++;
        }
        return ReachableShapes;
    }
}
