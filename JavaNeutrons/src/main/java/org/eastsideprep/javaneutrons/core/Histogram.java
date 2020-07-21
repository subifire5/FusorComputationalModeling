package org.eastsideprep.javaneutrons.core;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Histogram {

    int binsPerDecade = 10;
    double min;
    double max;
    double[] bins;
    boolean log;

    public Histogram(double min, double max, int bins, boolean log) {
        this.min = min;
        this.max = max;
        this.bins = new double[bins];
        this.log = log;
        //this.bins = new double[logMax - logMin + 1];
    }

    public Histogram() {
        this.min = -3;
        this.max = 7;
        this.bins = new double[(int) Math.ceil((max - min) * this.binsPerDecade)];
        this.log = true;
        //this.bins = new double[logMax - logMin + 1];
    }

//    public Histogram(boolean log) {
//        this.log = log;
//
//        if (log) {
//            this.min = -3;
//            this.max = 7;
//            this.bins = new double[(max - min) * this.binsPerDecade];
//        } else {
//            this.min = 25000;
//            this.max = 2525000;
//            this.bins = new double[(int)(this.max - this.min) / 25000];
//
//        }
//    }
    public void record(double value, double x) {
        int bin;

        if (this.log) {
            // take log in chosen base
            x = Math.log10(x);
        }
        x -= min;

        // find bin
        bin = (int) Math.ceil(x / (max - min) * (this.bins.length));

        // cut of stuff that is too small
        bin = Math.max(bin, 0);
        // cut off stuff that is too big
        bin = Math.min(bin, this.bins.length - 1);

        //System.out.println(""+this.hashCode()+": recording "+(x+min)+":"+value);
        synchronized (this) {
            this.bins[bin] += value;
        }
    }

    public XYChart.Series makeSeries(String seriesName) {

        return this.makeSeries(seriesName, 1.0);
    }

    public XYChart.Series makeSeries(String seriesName, double count) {
        //System.out.println("Retrieving series "+seriesName+":");
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        series.setName(seriesName);

        // put in all the data
        double[] counts = new double[this.bins.length];

        synchronized (this) {
            System.arraycopy(this.bins, 0, counts, 0, counts.length);
        }

        //System.out.println("");
        //System.out.println(""+this.hashCode()+Arrays.toString(bins));
        for (int i = 0; i < bins.length; i++) {
            double x = min + i / ((double) bins.length) * (max - min);
            if (this.log) {
                x = Math.pow(10, x);
            }
            String tick = String.format("%6.3e", x);
            data.add(new XYChart.Data(tick, counts[i] / count));
            //System.out.println(tick + " " + String.format("%6.3e", counts[i] / count));
        }
        //System.out.println("");

        return series;
    }

    protected interface DoubleTransform {

        double transform(double x);
    }

    protected interface XYTransform {

        double transform(double x, double y);
    }

    protected static class Identity {

        public static double x(double x) {
            return x;
        }

        public static double y(double x, double y) {
            return x;
        }
    }

    protected SimpleRegression regression( XYTransform ty) {
        SimpleRegression r = new SimpleRegression(true);

        // skip last bucket since it has the overflow
        for (int i = 0; i < bins.length - 1; i++) {
            double x = (min + i / ((double) bins.length) * (max - min));// * Util.Physics.eV;
            double y = bins[i];
            if (y == 0 || x == 0) {
                continue;
            }

            r.addData(x, ty.transform(x, y));
        }
        return r;
    }

    protected double RMSE(SimpleRegression r, DoubleTransform tx, XYTransform ity) {
        double vr = 0;
        double total = 0;
        // skip last bucket since it has the overflow
        for (int i = 0; i < bins.length - 1; i++) {
            double x = (min + i / ((double) bins.length) * (max - min));// * Util.Physics.eV;
            double y = bins[i];
            if (y == 0) {
                continue;
            }
            double yhat = ity.transform(x, r.predict(tx.transform(x)));
            if (Double.isNaN(yhat)) {
                System.out.println("");
            }
            double residual = y - yhat;
            vr += (residual * residual) / (bins.length - 1);
            total += y;
        }
        return Math.sqrt(vr / total);
    }

    public XYChart.Series makeFittedSeries(String seriesName, double count) {
        //System.out.println("Retrieving series "+seriesName+":");
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        series.setName(seriesName);

        // put in all the data
        double[] counts = new double[this.bins.length];

        synchronized (this) {
            System.arraycopy(this.bins, 0, counts, 0, counts.length);
        }

        XYTransform ty = null;
        XYTransform ity = null;

        if (seriesName.equals("Energy fit")) {
            ty = (x, yin) -> Math.log(yin / Math.sqrt(x));
            ity = (x, yout) -> Math.sqrt(x) * Math.exp(yout);
        } else if (seriesName.equals("Flux fit")){
            ty = (x, yin) -> Math.log(yin / x);
            ity = (x, yout) -> x * Math.exp(yout);
        } else {
            return null;
        }

        SimpleRegression r = this.regression(ty);

        //System.out.println("");
        //System.out.println(""+this.hashCode()+Arrays.toString(bins));
        for (int i = 0; i < bins.length; i++) {
            double x = (min + i / ((double) bins.length) * (max - min));// * Util.Physics.eV;
            if (this.log) {
                x = Math.pow(10, x);
            }
            double yActual = bins[i];
            double tyActual = ty.transform(x, yActual);
            double yPred = ity.transform(x, r.predict(x));
            String tick = String.format("%6.3e", x/*/ Util.Physics.eV*/);
            data.add(new XYChart.Data(tick, yPred/count));
            //System.out.println(tick + " " + String.format("%6.3e", counts[i] / count));
        }
        //System.out.println("");
        return series;
    }

    public void mutateNormalizeBy(Histogram other) {
        for (int i = 0; i < this.bins.length; i++) {
            if (other.bins[i] != 0) {
                bins[i] /= other.bins[i];
            }
        }
    }

    public void mutateClone(Histogram other) {
        System.arraycopy(other.bins, 0, bins, 0, bins.length);
    }

    public Histogram normalizeBy(Histogram other) {
        Histogram h = new Histogram(this.min, this.max, this.bins.length, false);
        h.mutateClone(this);
        h.mutateNormalizeBy(other);
        return h;
    }
}
