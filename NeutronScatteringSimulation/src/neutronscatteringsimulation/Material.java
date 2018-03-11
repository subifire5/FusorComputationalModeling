/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;

/**
 *
 * @author jfellows
 */
public class Material {

    private final Map<String, Double> massPercentages;
    private final HashMap<String, CrossSection> totals;
    private final HashMap<String, CrossSection> elastics;
    double density;

    public Material(double density, Map<String, Double> massPercentages) {
        totals = new HashMap<>();
        elastics = new HashMap<>();
        for (String k : massPercentages.keySet()) {
            totals.put(k, new CrossSection(k + " N,TOT"));
            elastics.put(k, new CrossSection(k + " N,EL"));
        }
        this.density = density; // g/cm3
        this.massPercentages = massPercentages;
    }
    
    public double sigmaTotal(Point3D R) {
        double sigma = 0;
        for (Map.Entry<String, Double> entry : massPercentages.entrySet()) {
            sigma += entry.getValue() * totals.get(entry.getKey()).get(R.magnitude());
        }
        sigma *= density;
        return sigma;
    }
    
    public double sigmaElasticScattering(Point3D R) {
        double sigma = 0;
        for (Map.Entry<String, Double> entry : massPercentages.entrySet()) {
            sigma += entry.getValue() * elastics.get(entry.getKey()).get(R.magnitude());
        }
        sigma *= density;
        return sigma;
    }

    private static class CrossSection {

        private final TreeMap<Double, Double> pointsMap;

        public CrossSection(String filename) {
            pointsMap = new TreeMap<>();
            try (Scanner scanner = new Scanner(new File("cross_sections/" + filename + ".txt"))) {
                String[] pieces;
                while (scanner.hasNextLine()) {
                   pieces = scanner.nextLine().trim().split("[\\s]+");
                   if (pieces[0].charAt(0) == '#') {
                       continue;
                   }
                   pointsMap.put(Double.valueOf(pieces[0]), Double.valueOf(pieces[1]));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public double get(Double energy) {
            // energy in eV
            Map.Entry<Double, Double> ceiling = pointsMap.ceilingEntry(energy);
            Map.Entry<Double, Double> floor = pointsMap.floorEntry(energy);
            double crossSection = ((ceiling.getValue() - floor.getValue()) * (energy - floor.getKey())) / (ceiling.getKey() - floor.getKey()) + floor.getValue(); // b
            return crossSection;
        }
    }

}
