/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LogHistogram {

    int logMin;
    int logMax;
    long[] bins;

    public LogHistogram(int logMin, int logMax, int bins) {
        this.logMin = logMin;
        this.logMax = logMax;
        this.bins = new long[bins];
    }

    public void record(double energy) {
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
            bin = this.bins.length-1;
        }
        
        // increment the count
        if (bin < 0 || bin >= this.bins.length) {
            System.out.println("hah!");
        }
        this.bins[bin]++;
    }

    public BarChart makeChart(boolean logCounts) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle("Energy spectrum");
        xAxis.setLabel("Energy (eV)");
        yAxis.setLabel(logCounts?"ln(1+count)":"Count");

        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        series.setName("Detector 1");

        // put in all the data
        for (int i = 0; i < this.bins.length; i++) {
            long count = this.bins[i];
            double value =  logCounts?(Math.log1p(this.bins[i])):this.bins[i];
            System.out.println(""+count+" "+value);
            data.add(new XYChart.Data(""+Math.pow(10, i + logMin),value));
        }

        bc.getData().add(series);
        return bc;
    }
}
