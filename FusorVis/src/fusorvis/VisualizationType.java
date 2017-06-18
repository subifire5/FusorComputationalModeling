/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;

import fusorcompmodeling.Point;
import javafx.scene.paint.Color;

/**
 *
 * @author guberti
 */
public abstract class VisualizationType {

    public VisualizationType(){};
    public abstract EFieldData calcPoint(Point[] points, Point p);
    public abstract void sortRefPoints(EFieldData[] refVals);
    public abstract Color calcColor(EFieldData[] refVals, EFieldData[][] data, EFieldData item);
    public abstract String toHumanReadable(EFieldData d);
    public void sortDoubleArray(EFieldData[] d) {
        EFieldData[] copy = d.clone();
        int moved = 0;
        
        while (moved < copy.length) {
            double min = Double.MAX_VALUE;
            
            for (int i = 0; i < copy.length; i++) {
                if (copy[i] != null) {
                    min = Math.min(min, copy[i].getDouble());
                    copy[i] = null;
                    moved++;
                }
            }
            
            d[moved - 1] = new EFieldData(min);
        }
        
    }

    double toColor(int index, int length) {
        // Return a val between 0.0 and 1.0
        return (double) index / (double) (length - 1);
    }
    
    int closestDoubleIndex(EFieldData[] arr, EFieldData item) {
        int low = 0;
        int high = arr.length - 1;
        
        double lastVal;
        int mid = -1;

        while (low <= high) {
        
            mid = (low + high) / 2;
            lastVal = arr[mid].d;
            
            if (item.d < lastVal) {
                high = mid - 1;
            } else if (item.d > lastVal) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return mid;
    }
}
