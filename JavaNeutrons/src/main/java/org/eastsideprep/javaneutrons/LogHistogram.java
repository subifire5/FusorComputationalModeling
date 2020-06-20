/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.Arrays;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class LogHistogram {

    int logMin;
    int logMax;
    double[] bins;

    public LogHistogram(int logMin, int logMax, int bins) {
        this.logMin = logMin;
        this.logMax = logMax;
        this.bins = new double[logMax - logMin + 1];
    }

    public void record(double value, double energy) {
        int bin;

        // take log in chosen base
        double logEnergy = Math.log10(energy) - logMin;

        // cut of stuff that is too small
        if (logEnergy < 0) {
            logEnergy = 0;
        }

        // find bin
        bin = (int) Math.round(logEnergy / (logMax - logMin) * this.bins.length);

        // cut off stuff that is too big
        if (bin >= this.bins.length) {
            bin = this.bins.length - 1;
        }

        // increment the count
        if (bin < 0 || bin >= this.bins.length) {
            System.out.println("hah!");
        }

        //System.out.println(""+this.hashCode()+": recording "+energy+":"+value);
        synchronized (this) {
            this.bins[bin] += value;
        }
    }

    public XYChart.Series makeSeries(String seriesName) {
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        series.setName(seriesName);

        // put in all the data
        double[] counts = new double[this.bins.length];

        synchronized (this) {
            System.arraycopy(this.bins, 0, counts, 0, counts.length);
        }
        
        //System.out.println(""+this.hashCode()+Arrays.toString(bins));

        for (int i = logMin; i < logMax; i++) {
            String tick = "" + Math.round(Math.pow(10, i));
            data.add(new XYChart.Data(tick, counts[i - logMin]));
            //System.out.println(""+this.hashCode() + tick +":"+ counts[i - logMin] + " ");
        }

        return series;
    }
}
