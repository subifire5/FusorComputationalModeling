/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author guberti
 */
public class PointDistributer {
    static Map<Integer, Double> totalSurfaceArea;
    static Map<Integer, double[][]> cumulativeSurfaceAreas;
    
    public PointDistributer() {
    }
    
    public static Point[] shakeUpPoints(List<GridComponent> parts, int pointsPerCharge, int endCon) {
        totalSurfaceArea = new HashMap<>();
        totalSurfaceArea.put(-1, totalSurfaceArea(parts, -1));
        totalSurfaceArea.put(1, totalSurfaceArea(parts, 1));
        
        calculateCumSurfaceAreas(parts);
        
        Point[] points = distributePoints(parts, pointsPerCharge);
        int reps = 0;
        while (reps < endCon) {
            int changesMade = balanceCharges(points, parts);
            reps++;
            System.out.println("Shook up points, " + reps + "/" + endCon + " shakes complete, made " + changesMade + " changes");
        }
        return points;
    }
    public static void calculateCumSurfaceAreas(List<GridComponent> parts) {
        int[] charges = {-1, 1};
        cumulativeSurfaceAreas = new HashMap<>();
        
        for (int charge : charges) {
            int partsIndex = 0;
            double cSAs [][] = new double[2][countPointsForCharge(parts, charge)];
            for (int i = 0; i < cSAs[0].length; i++) {
                // First, we must get the first part with the correct charge
                while (parts.get(partsIndex).charge != charge) {
                    partsIndex++;
                }
                cSAs[0][i] = parts.get(partsIndex).surfaceArea;
                
                if (i != 0) {
                    cSAs[0][i] += cSAs[0][i - 1];
                }
                
                cSAs[1][i] = partsIndex;
                
                partsIndex++;
            }
            cumulativeSurfaceAreas.put(charge, cSAs);
        }
    }
    
    private static int chargeToIndex(int charge) {
        if (charge == -1) {return 0;}
        return charge;
    }
    private static int countPointsForCharge(List<GridComponent> parts, int charge) {
        int count = 0;
        for (GridComponent p : parts) {
            if (p.charge == charge) {count++;}
        }
        return count;
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
        double area = totalSurfaceArea.get(charge);
        Random generator = new Random();

        double rand = generator.nextDouble() * area;
        int partIndex = getValAbove(rand, charge); // This is broken
        
        GridComponent g = parts.get(partIndex);
        Point p = g.getRandomPoint(generator);
        return p;
    }

    private static int getValAbove(double rand, int charge) {
        double[] arr = cumulativeSurfaceAreas.get(charge)[0];
        
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (high + low) / 2;
            
            if (arr[mid] >= rand && (mid == 0 || arr[mid-1] < rand)) { // If we're at the right location
                return (int) cumulativeSurfaceAreas.get(charge)[1][mid];
            } else if (arr[mid] < rand) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1; // Should never get here
    }
    
    public static double totalSurfaceArea(List<GridComponent> parts, int charge) {
        double surfaceArea = 0;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).charge == charge) {
                surfaceArea += parts.get(i).surfaceArea;
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
            Point newPoint = getRandomPoint(parts, points[i].charge);
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
}
