/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.ArrayList;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author jfellows
 */
public abstract class Block extends TriangleMesh {

    ArrayList<Point3D> points;
    ArrayList<Face> faces;

    public Block(TriangleMesh mesh, double input, double maxBump) {
        points = new ArrayList<>();
        for (int i = 0; i < mesh.getPoints().size(); i += 3) {
            points.add(new Point3D((double) mesh.getPoints().get(i), (double) mesh.getPoints().get(i + 1), (double) mesh.getPoints().get(i + 2)));
        }

        faces = new ArrayList<>();
        int i0, i1, i2;
        for (int i = 0; i < mesh.getFaces().size(); i += 6) {
            i0 = mesh.getFaces().get(i);
            i1 = mesh.getFaces().get(i + 2);
            i2 = mesh.getFaces().get(i + 4);
            triangulate(i0, i1, i2, input);
        }

        getTexCoords().addAll(0, 0);
        bumpify(maxBump);
        update();
    }

    double randomBump(double maxBump) {
        return NeutronScatteringSimulation.random.nextDouble() * maxBump;
    }

    void bumpify(double maxBump) {
        for (int i = 0; i < points.size(); i++) {
            // we want to shrink the block, so it doesn't end up intersecting neighboring blocks
            points.set(i, points.get(i).subtract(randomBump(maxBump), randomBump(maxBump), randomBump(maxBump)));
        }
    }

    abstract void triangulate(int i0, int i1, int i2, double maxArea);

    private void update() {
        getPoints().clear();
        for (Point3D point : points) {
            getPoints().addAll((float) point.getX(), (float) point.getY(), (float) point.getZ());
        }

        getFaces().clear();
        for (Face face : faces) {
            getFaces().addAll(face.p0, 0, face.p1, 0, face.p2, 0);
        }
    }

    public class Face {

        int p0, p1, p2;

        public Face(int p0, int p1, int p2) {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
        }
    }
}
