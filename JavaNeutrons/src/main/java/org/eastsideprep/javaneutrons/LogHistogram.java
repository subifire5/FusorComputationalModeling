/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LogHistogram {

    int logMin;
    int logMax;
    double[] bins;

    public LogHistogram(int logMin, int logMax, int bins) {
        this.logMin = logMin;
        this.logMax = logMax;
        this.bins = new double[bins];
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

        synchronized (this) {
            this.bins[bin] += value;
        }
    }

    public BarChart makeChart(String seriesName) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle("Energy spectrum");
        xAxis.setLabel("Energy (eV)");
        yAxis.setLabel("Count");

        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        series.setName(seriesName);

        // put in all the data
        double[] counts = new double[this.bins.length];
        
        synchronized (this) {
            System.arraycopy(this.bins, 0, counts, 0, counts.length);
        }
        
        for (int i = 0; i < counts.length; i++) {
            //System.out.println(""+count+" "+value);
            data.add(new XYChart.Data("" + Math.pow(10, i + logMin), counts[i]));
        }

        bc.getData().add(series);
        return bc;
    }
}
