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
public class Block extends TriangleMesh {

    ArrayList<Point3D> points;
    ArrayList<Face> faces;
    Point3D center;
    double radius;

    public Block(TriangleMesh mesh, Tiler tiler, double maxBump) {
        points = new ArrayList<>();
        for (int i = 0; i < mesh.getPoints().size(); i += 3) {
            points.add(new Point3D((double) mesh.getPoints().get(i), (double) mesh.getPoints().get(i + 1), (double) mesh.getPoints().get(i + 2)));
        }
        
        getBoundingSphere(points);
        buildFaces(mesh, tiler);

        getTexCoords().addAll(0, 0);
        bumpify(maxBump);
        update();
    }
    
    private void buildFaces(TriangleMesh mesh, Tiler tiler) {
        tiler.setBlock(this);
        faces = new ArrayList<>();
        int i0, i1, i2;
        for (int i = 0; i < mesh.getFaces().size(); i += 6) {
            i0 = mesh.getFaces().get(i);
            i1 = mesh.getFaces().get(i + 2);
            i2 = mesh.getFaces().get(i + 4);
            tiler.tile(i0, i1, i2);
        }
    }

    double randomBump(double maxBump) {
        return NeutronScatteringSimulation.random.nextDouble() * maxBump;
    }

    private void bumpify(double maxBump) {
        for (int i = 0; i < points.size(); i++) {
            // move the point in a random amounnt towards the center
            points.set(i, points.get(i).subtract(points.get(i).subtract(center).normalize().multiply(randomBump(maxBump))));
        }
    }

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

    private void getBoundingSphere(ArrayList<Point3D> P) {
        // an implementation of Riter's algorithm
        // does not always find the minimal bounding sphere
        Point3D x, y, z;
        x = P.get(0);
        y = maxDistance(x, P);
        z = maxDistance(y, P);
        center = y.midpoint(z);
        radius = y.distance(z) / 2;
        
        double difference;
        for (Point3D p : P) {
            difference = p.distance(center) - radius;
            if (difference > 0) {
                center = center.add(p.subtract(center).normalize().multiply(difference / 2));
                radius += difference / 2;
            }
        }
    }
    
    private Point3D maxDistance(Point3D p, ArrayList<Point3D> P) {
        double maxDistance = Double.MIN_VALUE;
        double distance;
        Point3D maxPoint = null;
        for (Point3D point : P) {
            distance = p.distance(point);
            if (distance > maxDistance) {
                maxPoint = point;
                maxDistance = distance;
            }
        }
        return maxPoint;
    }

    void move(Point3D v) {
        for (int i = 0; i < points.size(); i++) {
            points.set(i, points.get(i).add(v));
        }
        update();
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
