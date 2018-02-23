/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;

public class RecursiveTiler extends Block {
    
    public RecursiveTiler(TriangleMesh mesh, double maxArea, double maxBump) {
        super(mesh, maxArea, maxBump);
    }

    @Override
    void triangulate(int i0, int i1, int i2, double maxArea) {
        Point3D p0, p1, p2, centroid, a, b, normal;
        int ic;
        double area;
        p0 = points.get(i0);
        p1 = points.get(i1);
        p2 = points.get(i2);

        a = p0.subtract(p1);
        b = p0.subtract(p2);
        normal = a.crossProduct(b);
        area = .5 * normal.magnitude();
        if (area > maxArea) {
            centroid = p0.add(p1.add(p2)).multiply(1.0 / 3);
            ic = points.size();
            points.add(centroid);

            triangulate(i0, i1, ic, maxArea);
            triangulate(i1, i2, ic, maxArea);
            triangulate(i2, i0, ic, maxArea);
            return;
        }
        faces.add(new Face(i0, i1, i2));
    }
}
