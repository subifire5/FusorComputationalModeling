/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
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
        //
        long[] times = new long[logNums.length];
        System.out.println("Loading file...");
        XMLParser p = new XMLParser("SimpleXML.xml");
        List<GridComponent> parts = p.parseObjects();
        System.out.println("File loaded and initialized");
        Random rand = new Random();
        //-1 IS AN ANODE +, 1 IS A CATHODE -, 0 WILL BE NEUTRAL
        for (int i = 0; i < logNums.length; i++) {
            long startTime = System.currentTimeMillis();
            ArrayList<Point> points = distributePoints(parts, logNums[i]);
            Node root = Node.kdtree(points, 0);
            int changes = balanceCharges(root, parts, logNums[i]);
            System.out.println("Changes Made: " + changes);
            long endTime = System.currentTimeMillis();
            times[i] = endTime - startTime;
        }
        BufferedWriter logFile = null;
        try {
            logFile = new BufferedWriter(new FileWriter("C:\\Users\\Daman\\Documents\\TestFusor\\Test\\FusorLog.csv"));
            for (int i = 0; i < logNums.length; i++) {
                logFile.write("" + logNums[i] + "," + times[i]);
                logFile.newLine();
                logFile.flush();
            }
            logFile.close();
        } catch (Exception e) {
            System.err.println("Cannot create log file");

        }

    }

    public static ArrayList<Point> distributePoints(List<GridComponent> parts, int pointNum) {
        ArrayList<Point> totalPoints = new ArrayList<Point>();
        Random newRand = new Random();
        for (int i = 0; i < pointNum; i++) {
            totalPoints.add(getRandomPoint(parts));
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
            surfaceArea += parts.get(i).getSurfaceArea();
        }
        return surfaceArea;
    }

    public static double electricPotential(ArrayList<Node> nodes, Point comparePoint) {
        double positivePotential = 0;
        double negativePotential = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != null && !nodes.get(i).location.equals(comparePoint)) {
                if (nodes.get(i).location.charge == 1) {
                    positivePotential += (1 / distanceCalculator(nodes.get(i).location, comparePoint));
                } else {
                    negativePotential += (1 / distanceCalculator(nodes.get(i).location, comparePoint));
                }
            }
        }
        return (positivePotential - negativePotential);
    }

    public static double distanceCalculator(Point a, Point b) {
        double distance = Math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y)) + ((a.z - b.z) * (a.z - b.z)));
        return distance;
    }

    public static int balanceCharges(Node root, List<GridComponent> parts, int points) {
        int changes = 0;
        Deque<Node> results = new ArrayDeque<Node>();
        Node.search(root, results);
        Iterator j = results.iterator();
        while (j.hasNext()) {
            Node node = (Node) j.next();
            Point p = node.location;
            Point newPoint = getRandomPoint(parts);
            RectHV rect = new RectHV((p.x-2), (p.y-2), (p.x+2), (p.y+2), (p.z-2), (p.z+2));
            ArrayList<Node> nds = new ArrayList<Node>();
            Node.queryNode(root, rect, 0, nds);
            double currentEP = electricPotential(nds, node.location);
            double newEP = electricPotential(nds, newPoint);
            if (newEP < currentEP) {
                changes++;
                //points[i] = newPoint;
            } else if (newEP > currentEP) {
                //points[i].EP = currentEP;
            }
        }
        return changes;
    }

}
