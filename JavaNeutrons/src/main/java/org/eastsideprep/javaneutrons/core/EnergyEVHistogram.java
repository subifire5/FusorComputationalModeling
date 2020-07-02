/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;

/**
 *
 * @author gunnar
 */
public class EnergyEVHistogram extends Histogram {

    Histogram hFlat;

    public EnergyEVHistogram() {
        super(true);
        hFlat = new Histogram(false);
    }

    @Override
    public void record(double value, double energy) {
        super.record(value, energy / Util.Physics.eV);
        hFlat.record(value, energy / Util.Physics.eV);
    }

    public XYChart.Series makeSeries(String seriesName, boolean log) {
        return makeSeries(seriesName, 1, log);
    }

    public XYChart.Series makeSeries(String seriesName, double count, boolean log) {
        if (log) {
            return super.makeSeries(seriesName, count);
        }

        return hFlat.makeSeries(seriesName, count);
    }
}
