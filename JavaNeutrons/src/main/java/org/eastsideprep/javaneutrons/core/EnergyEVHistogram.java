package org.eastsideprep.javaneutrons.core;

import java.util.Arrays;
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

    public double getThermalEnergyMean(boolean log) {
        double total = 0;
        double totalCount = 0;
        Histogram h = log ? this : this.hFlat;

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // go through bins
        for (int i = 0; i < h.bins.length; i++) {
            double x = h.min + i / ((double) h.bins.length) * (h.max - h.min);
            if (log) {
                x = Math.pow(10, x);
            }
            if (x >= 1) {
                break;
            }
            total += x * counts[i];
            totalCount += counts[i];
        }
        return total / totalCount;
    }

    public double getThermalEnergyMode(boolean log) {
        double maxCount = 0;
        double maxAt = 0;
        Histogram h = log ? this : this.hFlat;

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // go through bins
        for (int i = 0; i < h.bins.length; i++) {
            double x = h.min + i / ((double) h.bins.length) * (h.max - h.min);
            if (log) {
                x = Math.pow(10, x);
            }
            if (x >= 1) {
                break;
            }
            if (counts[i] > maxCount) {
                maxCount = counts[i];
                maxAt = x;
            }
        }
        return maxAt;
    }

    public double getThermalEnergyMedian(boolean log) {
        Histogram h = log ? this : this.hFlat;
        double x = 0;

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // total
        double total = 0;
        for (int i = 0; i < h.bins.length; i++) {
            x = h.min + i / ((double) h.bins.length) * (h.max - h.min);
            if (log) {
                x = Math.pow(10, x);
            }
            if (x >= 1) {
                break;
            }
            total += counts[i];
        }

        // go through bins to find half of total counts
        double running = 0;
        for (int i = 0; i < h.bins.length; i++) {
            x = h.min + i / ((double) h.bins.length) * (h.max - h.min);
            if (log) {
                x = Math.pow(10, x);
            }
            if (x >= 1) {
                break;
            }
            running += counts[i];
            if (running + counts[i] > total / 2) {
                return x;
            }
        }
        return x;
    }

    public String getStatsString(boolean log) {
        return "Mean = " + String.format("%6.3e", getThermalEnergyMean(log)) + " eV, "
                + "Median = " + String.format("%6.3e", getThermalEnergyMedian(log)) + " eV, "
                + "Mode = " + String.format("%6.3e", getThermalEnergyMode(log)) + " eV";
    }
}
