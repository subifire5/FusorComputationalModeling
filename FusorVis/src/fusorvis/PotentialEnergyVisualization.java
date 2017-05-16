/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;

import fusorcompmodeling.Point;
import fusorcompmodeling.PointDistributer;
import java.util.Arrays;
import javafx.scene.paint.Color;

/**
 *
 * @author guberti
 */
public class PotentialEnergyVisualization extends VisualizationType {
    @Override
    public EFieldData calcPoint(Point[] points, Point p) {
        return new EFieldData(PointDistributer.electricPotential(points, p));
    }

    @Override
    public Color calcColor(EFieldData[] refVals, EFieldData[][] data, EFieldData item) {
        double gV = toColor(closestDoubleIndex(refVals, item), refVals.length);

        return new Color(gV, 0.0, 1.0 - gV, 1.0);
    }

    @Override
    public void sortRefPoints(EFieldData[] refVals) {
        sortDoubleArray(refVals);
    }
}
