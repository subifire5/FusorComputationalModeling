/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;

import fusorcompmodeling.Point;
import fusorcompmodeling.StatsGen;
import fusorcompmodeling.Vector;
import javafx.scene.paint.Color;

/**
 *
 * @author guberti
 */
public class IntensitiesVisualization extends VisualizationType {

    @Override
    public EFieldData calcPoint(Point[] points, Point p) {
        return new EFieldData(getInten(StatsGen.FToAcc(points, p, 1, new Vector(0, 0, 0))));
    }

    @Override
    public void sortRefPoints(EFieldData[] refVals) {
        sortDoubleArray(refVals);
    }

    @Override
    public Color calcColor(EFieldData[] refVals, EFieldData[][] data, EFieldData item) {
        double gV = toColor(closestDoubleIndex(refVals, item), refVals.length);
        
        return new Color(gV, gV, gV, 1);
    }
    
    private double getInten(Vector v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
    }

    @Override
    public String toHumanReadable(EFieldData d) {
        return Double.toString(d.d);
    }
}
