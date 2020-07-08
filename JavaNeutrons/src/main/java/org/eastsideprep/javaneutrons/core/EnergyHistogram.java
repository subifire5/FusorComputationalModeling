package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class EnergyHistogram extends Histogram {

    Histogram hFlat;
    Histogram hLow;

    public EnergyHistogram() {
        super(-3, 7, 100, true);
        hFlat = new Histogram(15000, 3e6, 199, false);
        hLow = new Histogram(1e-3, 0.15, 149, false);
    }

    @Override
    public void record(double value, double energy) {
        super.record(value, energy / Util.Physics.eV);
        hFlat.record(value, energy / Util.Physics.eV);
        hLow.record(value, energy / Util.Physics.eV);
    }

    public XYChart.Series makeSeries(String seriesName, String scale) {
        return makeSeries(seriesName, 1, scale);
    }

    public XYChart.Series makeSeries(String seriesName, double count, String scale) {
        switch (scale) {
            case "Log":
                return super.makeSeries(seriesName, count);
            case "Linear (all)":
                return hFlat.makeSeries(seriesName, count);
            default:
                //return hFlat.makeSeries(seriesName, count);
                return hLow.makeSeries(seriesName, count);
        }
    }

    public Histogram getHistogram(String scale) {
        switch (scale) {
            case "Log":
                return this;
            case "Linear (all)":
                return hFlat;
            default:
                //return hFlat.makeSeries(seriesName, count);
                return hLow;
        }
    }

    public double getThermalEnergyMean(String scale) {
        double total = 0;
        double totalCount = 0;
        boolean log = scale.equals("Log");
        Histogram h = getHistogram(scale);

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // go through bins
        for (int i = 0; i < h.bins.length-1; i++) {
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

    public double getThermalEnergyMode(String scale) {
        Histogram h = getHistogram(scale);
        boolean log = scale.equals("Log");
        double maxCount = 0;
        double maxAt = 0;

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // go through bins
        for (int i = 0; i < h.bins.length-1; i++) {
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

    public double getThermalEnergyMedian(String scale) {
        boolean log = scale.equals("Log");
        Histogram h = getHistogram(scale);
        double x = 0;

        // put in all the data
        double[] counts = new double[h.bins.length];
        synchronized (this) {
            System.arraycopy(h.bins, 0, counts, 0, counts.length);
        }

        // total
        double total = 0;
        for (int i = 0; i < h.bins.length-1; i++) {
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
        for (int i = 0; i < h.bins.length-1; i++) {
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

    public String getStatsString(String scale) {
        String result = "Mean = " + String.format("%6.3e", getThermalEnergyMean(scale)) + " eV, "
                + "Median = " + String.format("%6.3e", getThermalEnergyMedian(scale)) + " eV, "
                + "Mode = " + String.format("%6.3e", getThermalEnergyMode(scale)) + " eV";
        if (scale.equals("Linear (thermal)")) {
            result += "\nMaxwell-Boltzmann fit: " + this.fitMaxwell();
        }
        return result;
    }

    public String fitMaxwell() {
        SimpleRegression r = new SimpleRegression(true);

        // skip first aand last bucket since they have the under- and overflow
        for (int i = 1; i < hLow.bins.length - 1; i++) {
            double x = hLow.min + i / ((double) hLow.bins.length) * (hLow.max - hLow.min);
            double y = hLow.bins[i];
            if (y == 0 || x == 0) {
                System.out.println("invalid data for x = " + x);
            }

            //r.addData(x * Util.Physics.eV, Math.log(y / Math.sqrt(x)));
            r.addData(x * Util.Physics.eV, Math.log(y / x));
        }

        String result = "y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                + "*E*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.boltzmann * Util.Physics.roomTemp) + "*E/(kb*t))";
        
        
        // skip first aand last bucket since they have the under- and overflow
        for (int i = 1; i < hLow.bins.length - 1; i++) {
            double x = hLow.min + i / ((double) hLow.bins.length) * (hLow.max - hLow.min);
            double y = hLow.bins[i];
            if (y == 0 || x == 0) {
                System.out.println("invalid data for x = " + x);
            }

            r.addData(x * Util.Physics.eV, Math.log(y / Math.sqrt(x)));
            //r.addData(x * Util.Physics.eV, Math.log(y / x));
        }
          result += "\ny = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                + "*sqrt(E)*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.boltzmann * Util.Physics.roomTemp) + "*E/(kb*t))";
        
       
        
        System.out.println("Regression result: " + result);

        return result;
    }
}
