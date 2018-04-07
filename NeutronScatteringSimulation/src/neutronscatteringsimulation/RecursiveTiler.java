/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import javafx.geometry.Point3D;

public class RecursiveTiler implements Tiler {
    
    final double MAX_AREA;
    Block block;
    
    public RecursiveTiler(double MAX_AREA) {
        this.MAX_AREA = MAX_AREA;
    }
    
    @Override
    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public void tile(int i0, int i1, int i2) {
        Point3D p0, p1, p2, centroid, a, b, normal;
        int ic;
        double area;
        p0 = block.points.get(i0);
        p1 = block.points.get(i1);
        p2 = block.points.get(i2);

        a = p0.subtract(p1);
        b = p0.subtract(p2);
        normal = a.crossProduct(b);
        area = .5 * normal.magnitude();
        if (area > MAX_AREA) {
            centroid = p0.add(p1.add(p2)).multiply(1.0 / 3);
            ic = block.points.size();
            block.points.add(centroid);

            tile(i0, i1, ic);
            tile(i1, i2, ic);
            tile(i2, i0, ic);
            return;
        }
        block.faces.add(block.new Face(i0, i1, i2));
    }
}
