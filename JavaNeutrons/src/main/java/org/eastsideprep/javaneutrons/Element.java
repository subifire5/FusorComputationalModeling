/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author taras
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
    String name;
    double mass; // g
    int atomicNumber;
    int neutrons;
    private ArrayList<Entry> entries;
    
    Element(String name, double mass, int atomicNumber, int neutrons) {
        this.name = name;
        this.mass = mass;
        this.atomicNumber = atomicNumber;
        this.neutrons = neutrons;
        this.entries = new ArrayList<Entry>();
    }
    void fillEntries(String fileName) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        
        this.entries = new ArrayList<Entry>(); //reset
        String line = br.readLine();
        while (!line.equals("")) {
            String[] split = line.split(",");
            double energy = Double.parseDouble(split[0]);
            double area = Double.parseDouble(split[1]);
            entries.add(new Entry(energy, area));
            line = br.readLine();
        }
        Collections.sort(entries, (a,b) -> {
            double diff = a.energy - b.energy;
            if(diff > 0) {
                return 1;
            }
            if(diff < 0) {
                return -1;
            }
            return 0;
        });
    }
    double getArea(double energy) {
        int index = Collections.binarySearch(entries, new Entry(energy, 0), (a,b) -> {
            double diff = a.energy - b.energy;
            if(diff > 0) {
                return 1;
            }
            if(diff < 0) {
                return -1;
            }
            return 0;
        });
        if(index >= 0) {
            return entries.get(index).area;
        }
        //else, linear interpolate between two nearest points
        index = -index-1;
        if(index == 0 || index >= entries.size()) {
            System.out.println("Not enough data to linear interpolate");
            return -1;
        }
        Entry e1 = entries.get(index-1); //the one with just lower energy
        Entry e2 = entries.get(index);   //the one with just higher energy
        double area = e1.area + ( ((energy-e1.energy)/(e2.energy-e1.energy)) * (e2.area-e1.area) ); //linear interpolation function
        return area;
    }    
    // todo how do we associate neutron cross-sections with this
    
}
