/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Detector extends Part {

    public LogHistogram entryEnergies;
    public LogHistogram fluenceOverEnergy;
    private final double volume;
    private double currentEntryEnergy = 0;
    private double totalDepositedEnergy = 0;

    public Detector(String name) {
        super(name, null, null);
        this.volume = Double.MAX_VALUE;
        reset();
    }

    public Detector(String name, Shape shape) {
        // todo: material (some simple one-element stuff?)
        super(name, shape, Unobtainium.getInstance());

        this.volume = this.shape.getVolume();
        reset();
    }

    @Override
    void processPathLength(double length, double energy) {
        //System.out.println("Entry into detector path length log " + this.entryEnergies.hashCode());

        this.fluenceOverEnergy.record(length / volume, energy);
    }

    @Override
    void processEntryEnergy(double e) {
        //System.out.println("Entry into detector entry energy log " + this.entryEnergies.hashCode());
        this.entryEnergies.record(1, e);
        this.currentEntryEnergy = e;
    }

    @Override
    void processExitEnergy(double e) {
        this.totalDepositedEnergy += (e - this.currentEntryEnergy);
    }

    public double getTotalDepositedEnergy() {
        return this.totalDepositedEnergy;
    }

    public void reset() {
        this.currentEntryEnergy = 0;
        this.totalDepositedEnergy = 0;
        this.entryEnergies = new LogHistogram(-5, 10, 50);
        this.fluenceOverEnergy = new LogHistogram(-5, 10, 50);
    }

}
