package org.eastsideprep.javaneutrons.core;

import javafx.scene.chart.XYChart;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class EnergyHistogram extends Histogram {

    public static double LOW_VISUAL_LIMIT = 0.15;
    static double LOW_TRACKING_LIMIT = 0.15;
    static double LOW_BIN_SIZE = 1e-3;

    Histogram hFlat;
    Histogram hLow;
    double [] energyFitParams = new double[] {0,0};
    double [] fluxFitParams = new double[] {0,0};

    public EnergyHistogram() {
        super(-3, 7, 100, true);
        hFlat = new Histogram(15000, 3e6, 199, false);
        hLow = new Histogram(LOW_BIN_SIZE, LOW_TRACKING_LIMIT, (int) (LOW_TRACKING_LIMIT / LOW_BIN_SIZE) - 1, false);
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
                return hLow.makeSeries(seriesName, count, LOW_VISUAL_LIMIT);
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

//        if (scale.equals("Linear (thermal)")) {
//            result += this.fitDistributions(flux, count);
//        }
        if (result.contains("NaN")) {
            result = "";
        }
        return result;
    }

    public String fitDistributions(boolean flux, long count) {
        SimpleRegression r;
        double RMSE;
        String result = "";

        if (flux) {
            double[] beta = fitFlux(count);
            result += "\nFlux distribution fit: y ~ " //+String.format("%6.3e", beta[0])
                    + "E*exp(" + String.format("%5.3f", beta[1] * Util.Physics.kB * Util.Physics.T / Util.Physics.eV)
                    + "*E/(kb*t))";
        } else {
            double[] beta = fitEnergy(count);
            result += "\nEnergy distribution fit: y ~ " //+ String.format("%6.3e", beta[0]) + "*"
                    + "sqrt(E)"
                    + "*exp(" + String.format("%5.3f", beta[1] * Util.Physics.kB * Util.Physics.T / Util.Physics.eV)
                    + "*E/(kb*t))";
        }
        System.out.println(result);

        return result;
    }

    private class MaxwellianEnergyDistribution implements ParametricUnivariateFunction {

        @Override
        public double value(double x, double... params) {
            double a = params[0];
            double b = params[1];

            return a * Math.sqrt(x) * Math.exp(b * x);
        }

        @Override
        public double[] gradient(double x, double... params) {
            double a = params[0];
            double b = params[1];

            return new double[]{Math.sqrt(x) * Math.exp(b * x), a * Math.pow(x, 3.0 / 2.0) * Math.exp(b * x)};
        }

    }

    private class FluxDistribution implements ParametricUnivariateFunction {

        @Override
        public double value(double x, double... params) {
            double a = params[0];
            double b = params[1];

            return a * x * Math.exp(b * x);
        }

        @Override
        public double[] gradient(double x, double... params) {
            double a = params[0];
            double b = params[1];

            return new double[]{x * Math.exp(b * x), a * x * x * Math.exp(b * x)};
        }

    }

    public double[] fitEnergy(double count) {
        double[] beta = hLow.fitCurve(new MaxwellianEnergyDistribution(), new double[]{100, 1}, count);

        System.out.println("y = " + beta[0] + "*sqrt(x)*exp(" + beta[1] + "*x)");
        this.energyFitParams = beta;
        return beta;

    }
      public double[] fitFlux(double count) {
        double[] beta = hLow.fitCurve(new FluxDistribution(), new double[]{100, 1}, count);

        System.out.println("y = " + beta[0] + "*x*exp(" + beta[1] + "*x)");
        this.fluxFitParams = beta;
        return beta;

    }
    
      public XYChart.Series makeFittedSeries(String seriesName, double count) {
          if (seriesName.equals("Energy fit")) {
              return hLow.makeFittedSeries(seriesName, new MaxwellianEnergyDistribution(), this.energyFitParams, count, EnergyHistogram.LOW_VISUAL_LIMIT);
          } else  if (seriesName.equals("Flux fit")) {
              return hLow.makeFittedSeries(seriesName, new FluxDistribution(), this.fluxFitParams, count, EnergyHistogram.LOW_VISUAL_LIMIT);
          } else {
              return null;
          }
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
