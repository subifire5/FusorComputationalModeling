package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class EnergyHistogram extends Histogram {

    Histogram hFlat;
    Histogram hLow;

    public EnergyHistogram() {
        super(-3, 7, 100, true);
        hFlat = new Histogram(15000, 3e6, 199, false);
        double cut = 0.15;
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

    public String getStatsString(String scale, long count) {
        String result = "Mean = " + String.format("%6.3e", getThermalEnergyMean(scale)) + " eV, "
                + "Median = " + String.format("%6.3e", getThermalEnergyMedian(scale)) + " eV, "
                + "Mode = " + String.format("%6.3e", getThermalEnergyMode(scale)) + " eV";
        if (scale.equals("Linear (thermal)")) {
            result += "\n" + this.fitDistributions(count);
        }
        return result;
    }

    private interface DoubleTransform {

        double transform(double x);
    }

    private interface XYTransform {

        double transform(double x, double y);
    }

    private static class Identity {

        public static double x(double x) {
            return x;
        }

        public static double y(double x, double y) {
            return x;
        }
    }

    private SimpleRegression regression(DoubleTransform tx, XYTransform ty) {
        SimpleRegression r = new SimpleRegression(true);

        // skip last bucket since it has the overflow
        for (int i = 0; i < hLow.bins.length - 1; i++) {
            double x = (hLow.min + i / ((double) hLow.bins.length) * (hLow.max - hLow.min)) * Util.Physics.eV;
            double y = hLow.bins[i];
            if (y == 0 || x == 0) {
                continue;
            }

            r.addData(tx.transform(x), ty.transform(x, y));
        }
        return r;
    }

    private double RMSE(SimpleRegression r, DoubleTransform tx, XYTransform ity) {
        double vr = 0;
        double total = 0;
        // skip last bucket since it has the overflow
        for (int i = 0; i < hLow.bins.length - 1; i++) {
            double x = (hLow.min + i / ((double) hLow.bins.length) * (hLow.max - hLow.min)) * Util.Physics.eV;
            double y = hLow.bins[i];
            if (y == 0) {
                continue;
            }
            double residual = y - ity.transform(x, r.predict(tx.transform(x)));
            vr += residual * residual / hLow.bins.length;
            total += y;
        }
        return Math.sqrt(vr/total);
    }

    public String fitDistributions(long count) {
        SimpleRegression r;
        double RMSE;
        String result = "";

        r = regression(Identity::x, (x, y) -> Math.log(y));
        RMSE = RMSE(r, Identity::x, (x,y)->Math.exp(y));
        result += "Gaussian distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                + "*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.kB * Util.Physics.T)
                + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e",RMSE/count)+ "\n";

        r = regression(Identity::x, (x, y) -> (y * y));
        RMSE = RMSE(r, Identity::x, (x,y)->Math.sqrt(y));
        result += "Path length distribution fit: y = sqrt("
                + String.format("%6.3e", r.getSlope())
                + "*E), normalized RMSE = " + String.format("%6.3e",RMSE/count) + "\n";

        r = regression(Identity::x, (x, y) -> Math.log(y / x));
        RMSE = RMSE(r, Identity::x, (x,y)->x*Math.exp(y));
        result += "Flux distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                + "*E*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.kB * Util.Physics.T)
                + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e",RMSE/count) + "\n";

        r = regression(Identity::x, (x, y) -> Math.log(y / Math.sqrt(x)));
        RMSE = RMSE(r, Identity::x, (x,y)->Math.sqrt(x)*Math.exp(y));
        result += "Energy distribution fit: y = " + String.format("%6.3e", Math.exp(r.getIntercept()))
                + "*sqrt(E)*exp(" + String.format("%6.3e", r.getSlope() * Util.Physics.kB * Util.Physics.T)
                + "*E/(kb*t)), normalized RMSE = " + String.format("%6.3e",RMSE/count) + "";

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
