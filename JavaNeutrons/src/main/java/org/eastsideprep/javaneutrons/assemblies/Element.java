/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.assemblies;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import org.eastsideprep.javaneutrons.core.Neutron;
import org.eastsideprep.javaneutrons.core.Util;

/**
 *
 * @author gunnar
 */
public class Element {

    private class Entry {

        double energy;
        double area;

        private Entry(double energy, double area) {
            this.energy = energy;
            this.area = area;
        }
    }
    
    private static HashMap<String, Element> elements = new HashMap<>();

    public String name;
    public double mass; // g
    protected int atomicNumber;
    protected int neutrons;
    private ArrayList<Entry> elasticEntries;
    private ArrayList<Entry> totalEntries;

    // for when you are too lazy to look up the correct mass
    public Element(String name, int atomicNumber, int neutrons) {
        this(name, atomicNumber, neutrons, atomicNumber * Util.Physics.protonMass + neutrons * Neutron.mass);
    }
    
    // use this when you know the mass in kg
    public Element(String name, int atomicNumber, int neutrons, double mass) {
        Element.elements.put(name, this);
        
        this.atomicNumber = atomicNumber;
        this.neutrons = neutrons;

        // todo: how wrong is this, quantum-wise?
        this.mass = mass;

        // read appropriate ENDF-derived data file
        // for the lightest stable isotope of the element
        readDataFiles("" + (atomicNumber) + "25");
    }
    
    
    public static Element getByName(String name) {
        return Element.elements.get(name);
    }

    public double getScatterCrossSection(double energy) {
        return getArea(elasticEntries, energy);
    }

    public double getCaptureCrossSection(double energy) {
        return getArea(totalEntries, energy) - getArea(elasticEntries, energy);
    }

    public double getTotalCrossSection(double energy) {
        return getArea(totalEntries, energy);
    }

    protected final void readDataFiles(String name) {
        this.elasticEntries = fillEntries(name, "elastic");
        this.totalEntries = fillEntries(name, "total");
    }

    //
    // kind is "elastic" or "total"
    //
    private ArrayList<Entry> fillEntries(String fileName, String kind) {

        // read xyz.csv from resources/data
        InputStream is = Element.class.getResourceAsStream("/data/" + kind + "/" + fileName + ".csv");
        if (is == null) {
            System.out.println("Data file " + fileName + " (" + kind + ") not found for element "+this.name);
            return null;
        }
        Scanner sc = new Scanner(is);

        ArrayList<Entry> newEntries = new ArrayList<>(); //reset
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] split = line.split(",");
            double energy = Double.parseDouble(split[0]);
            double area = Double.parseDouble(split[1]);
            newEntries.add(new Entry(energy, area));
        }
        Collections.sort(newEntries, (a, b) -> {
            return (int) Math.signum(a.energy - b.energy);
        });

        return newEntries;
    }

    private double getArea(ArrayList<Entry> data, double energy) {
        // table data is in eV, convert to SI (cm)
        energy /= Util.Physics.eV;
        //System.out.println("Energy: "+energy+" eV");
        int index = Collections.binarySearch(data, new Entry(energy, 0), (a, b) -> (int) Math.signum(a.energy - b.energy));
        if (index >= 0) {
            return data.get(index).area;
        }
        //else, linear interpolate between two nearest points
        index = -index - 1;
        if (index == 0 || index >= data.size()) {
            // todo: Our neutrons should not get this cold,
            // but if they do, deal with it properly
            // for now, just return the smallest cross-section
            //System.out.println("Not enough data to linear interpolate");
            return data.get(0).area;
        }
        Entry e1 = data.get(index - 1); //the one with just lower energy
        Entry e2 = data.get(index);   //the one with just higher energy
        double area = e1.area + (((energy - e1.energy) / (e2.energy - e1.energy)) * (e2.area - e1.area)); //linear interpolation function

        // convert back into SI (cm) and return
        return area * Util.Physics.barn;
    }
}
