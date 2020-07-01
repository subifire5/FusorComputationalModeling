package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;

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
