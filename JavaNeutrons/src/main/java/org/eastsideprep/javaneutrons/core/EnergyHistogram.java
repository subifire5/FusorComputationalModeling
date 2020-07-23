package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class EnergyHistogram extends Histogram {

    Histogram hFlat;
    Histogram hLow;

    public EnergyHistogram() {
        super(-3, 7, 100, true);
        hFlat = new Histogram(15000, 3e6, 199, false);
        double cut = 0.20;
        hLow = new Histogram(1e-3, cut, (int) (cut * 1000) - 1, false);
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
            case "Linear (thermal)":
                return hLow.makeSeries(seriesName, count);
            case "Linear (thermal fit)":
                return hLow.makeFittedSeries(seriesName, count);
            case "Linear (thermal energy fit)":
                return hLow.makeFittedSeries(seriesName, count);
            default:
                //return hFlat.makeSeries(seriesName, count);
                return null;
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
        for (int i = 0; i < h.bins.length - 1; i++) {
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
        for (int i = 0; i < h.bins.length - 1; i++) {
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
        for (int i = 0; i < h.bins.length - 1; i++) {
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
        for (int i = 0; i < h.bins.length - 1; i++) {
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

    public String getStatsString(String scale, boolean flux, long count) {
        String result = "";
//        result += "Mean = " + String.format("%6.3e", getThermalEnergyMean(scale)) + " eV, "
//                + "Median = " + String.format("%6.3e", getThermalEnergyMedian(scale)) + " eV, "
//                + "Mode = " + String.format("%6.3e", getThermalEnergyMode(scale)) + " eV";
        if (scale.equals("Linear (thermal)")) {
            result += "\nThermal energy stats:\n" + this.fitDistributions(flux, count);
        }
        return result;
    }

    public String fitDistributions(boolean flux, long count) {
        SimpleRegression r;
        double RMSE;
        String result = "";

//        r = hLow.regression((x, y) -> Math.log(y));
//        RMSE = hLow.RMSE(r, Identity::x, (x, y) -> Math.exp(y));
//        result += "Gaussian distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
//                + "*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.kB * Util.Physics.T)
//                + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e", RMSE / count) + "\n";
//
//        r = hLow.regression((x, y) -> (y * y));
//        RMSE = hLow.RMSE(r, Identity::x, (x, y) -> Math.sqrt(y));
//        result += "Path length distribution fit: y = sqrt("
//                + String.format("%6.3e", r.getSlope())
//                + "*E), normalized RMSE = " + String.format("%6.3e", RMSE / count) + "\n";
        if (flux) {
            r = hLow.regression((x, y) -> Math.log(y / x));
            RMSE = hLow.RMSE(r, Identity::x, (x, y) -> x * Math.exp(y));
            result += "Flux distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                    + "*E*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.kB * Util.Physics.T)
                    + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e", RMSE / count) + "\n";
        } else {
            r = hLow.regression((x, y) -> Math.log((y * Util.Physics.kB * Util.Physics.T) / Math.sqrt(x)));
            RMSE = hLow.RMSE(r, Identity::x, (x, y) -> Math.sqrt(x) * Math.exp(y) / (Util.Physics.kB * Util.Physics.T));
            result += "Energy distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                    + "*sqrt(E)*exp(" + String.format("%6.3e", r.getSlope())
                    + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e", RMSE / count) + "";
        }
        System.out.println(result);

        return result;
    }

    public EnergyHistogram normalizeBy(EnergyHistogram other) {
        EnergyHistogram h = new EnergyHistogram();
        h.mutateClone(this);
        h.mutateNormalizeBy(other);
        h.hFlat.mutateClone(this.hFlat);
        h.hFlat.mutateNormalizeBy(other.hFlat);
        h.hLow.mutateClone(this.hLow);
        h.hLow.mutateNormalizeBy(other.hLow);
        return h;
    }
}
