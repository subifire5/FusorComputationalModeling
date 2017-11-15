/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.ArrayList;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author jfellows
 */
public class ParaffinBlock extends TriangleMesh {

    ArrayList<Vector3> points;
    ArrayList<Face> faces;

    public ParaffinBlock(double width, double length, double depth, double triSize, double maxBump) {
        points = new ArrayList<>();
        faces = new ArrayList<>();
        ArrayList<Side> sides = new ArrayList<>();
        int numWidth = (int) (width / triSize) + 1;
        int numLength = (int) (length / triSize) + 1;
        int numDepth = (int) (depth / triSize) + 1;
        sides.add(new Side(numWidth, numLength));
        sides.add(new Side(numWidth, numLength));
        sides.add(new Side(numWidth, numDepth));
        sides.add(new Side(numWidth, numDepth));
        sides.add(new Side(numLength, numDepth));
        sides.add(new Side(numLength, numDepth));

        int i = 0;
        Vector3 p;
        double x, y, z;
        for (x = 0; x <= width; x += triSize) {
            for (y = 0; y <= length; y += triSize) {
                p = new Vector3(x, y, 0);
                p.z += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(0).add(p);
                p = new Vector3(x, y, depth);
                p.z += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(1).add(p);
            }
            for (z = 0; z <= depth; z += triSize) {
                p = new Vector3(x, 0, z);
                p.y += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(2).add(p);
                p = new Vector3(x, length, z);
                p.y += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(3).add(p);
            }
        }
        for (y = 0; y <= length; y += triSize) {
            for (z = 0; z <= depth; z += triSize) {
                p = new Vector3(0, y, z);
                p.x += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(4).add(p);
                p = new Vector3(width, y, z);
                p.x += (NeutronScatteringSimulation.random.nextDouble() * maxBump * 2) - maxBump;
                p.i = i++;
                points.add(p);
                sides.get(5).add(p);
            }
        }

        boolean side = true;
        for (i = 0; i < numWidth; i++) {
            if (side) {
                points.set(sides.get(0).get(i, 0).i, sides.get(2).get(i, 0));
                points.set(sides.get(1).get(i, 0).i, sides.get(2).get(i, numDepth - 1));
                points.set(sides.get(0).get(i, numLength - 1).i, sides.get(3).get(i, 0));
                points.set(sides.get(1).get(i, numLength - 1).i, sides.get(3).get(i, numDepth - 1));
            } else {
                points.set(sides.get(2).get(i, 0).i, sides.get(0).get(i, 0));
                points.set(sides.get(2).get(i, numDepth - 1).i, sides.get(1).get(i, 0));
                points.set(sides.get(3).get(i, 0).i, sides.get(0).get(i, numLength - 1));
                points.set(sides.get(3).get(i, numDepth - 1).i, sides.get(1).get(i, numLength - 1));
            }
            side = NeutronScatteringSimulation.random.nextBoolean();
        }

        for (i = 0; i < numLength; i++) {
            if (side) {
                points.set(sides.get(0).get(numWidth - 1, i).i, sides.get(5).get(i, 0));
                points.set(sides.get(0).get(0, i).i, sides.get(4).get(i, 0));
                points.set(sides.get(1).get(0, i).i, sides.get(4).get(i, numDepth - 1));
                points.set(sides.get(1).get(numWidth - 1, i).i, sides.get(5).get(i, numDepth - 1));
            } else {
                points.set(sides.get(5).get(i, 0).i, sides.get(0).get(numWidth - 1, i));
                points.set(sides.get(4).get(i, 0).i, sides.get(0).get(0, i));
                points.set(sides.get(4).get(i, numDepth - 1).i, sides.get(1).get(0, i));
                points.set(sides.get(5).get(i, numDepth - 1).i, sides.get(1).get(numWidth - 1, i));
            }
            side = NeutronScatteringSimulation.random.nextBoolean();
        }

        for (i = 0; i < numDepth; i++) {
            if (side) {
                points.set(sides.get(2).get(0, i).i, sides.get(4).get(0, i));
                points.set(sides.get(3).get(0, i).i, sides.get(4).get(numLength - 1, i));
                points.set(sides.get(2).get(numWidth - 1, i).i, sides.get(5).get(0, i));
                points.set(sides.get(3).get(numWidth - 1, i).i, sides.get(5).get(numLength - 1, i));
            } else {
                points.set(sides.get(4).get(0, i).i, sides.get(2).get(0, i));
                points.set(sides.get(4).get(numLength - 1, i).i, sides.get(3).get(0, i));
                points.set(sides.get(5).get(0, i).i, sides.get(2).get(numWidth - 1, i));
                points.set(sides.get(5).get(numLength - 1, i).i, sides.get(3).get(numWidth - 1, i));
            }
            side = NeutronScatteringSimulation.random.nextBoolean();
        }

        points.set(sides.get(2).get(0, 0).i, sides.get(0).get(0, 0));
        points.set(sides.get(4).get(0, 0).i, sides.get(0).get(0, 0));
        points.set(sides.get(0).get(0, 0).i, sides.get(0).get(0, 0));

        points.set(sides.get(0).get(numWidth - 1, 0).i, sides.get(5).get(0, 0));
        points.set(sides.get(2).get(numWidth - 1, 0).i, sides.get(5).get(0, 0));
        points.set(sides.get(5).get(0, 0).i, sides.get(5).get(0, 0));

        points.set(sides.get(0).get(0, numLength - 1).i, sides.get(3).get(0, 0));
        points.set(sides.get(3).get(0, 0).i, sides.get(3).get(0, 0));
        points.set(sides.get(4).get(numLength - 1, 0).i, sides.get(3).get(0, 0));

        points.set(sides.get(0).get(numWidth - 1, numLength - 1).i, sides.get(3).get(numWidth - 1, 0));
        points.set(sides.get(3).get(numWidth - 1, 0).i, sides.get(3).get(numWidth - 1, 0));
        points.set(sides.get(5).get(numLength - 1, 0).i, sides.get(3).get(numWidth - 1, 0));

        points.set(sides.get(1).get(0, 0).i, sides.get(1).get(0, 0));
        points.set(sides.get(2).get(0, numDepth - 1).i, sides.get(1).get(0, 0));
        points.set(sides.get(4).get(0, numDepth - 1).i, sides.get(1).get(0, 0));

        points.set(sides.get(1).get(numWidth - 1, 0).i, sides.get(2).get(numWidth - 1, numDepth - 1));
        points.set(sides.get(2).get(numWidth - 1, numDepth - 1).i, sides.get(2).get(numWidth - 1, numDepth - 1));
        points.set(sides.get(5).get(0, numDepth - 1).i, sides.get(2).get(numWidth - 1, numDepth - 1));

        points.set(sides.get(1).get(0, numLength - 1).i, sides.get(4).get(numLength - 1, numDepth - 1));
        points.set(sides.get(3).get(0, numDepth - 1).i, sides.get(4).get(numLength - 1, numDepth - 1));
        points.set(sides.get(4).get(numLength - 1, numDepth - 1).i, sides.get(4).get(numLength - 1, numDepth - 1));

        points.set(sides.get(1).get(numWidth - 1, numLength - 1).i, sides.get(5).get(numLength - 1, numDepth - 1));
        points.set(sides.get(3).get(numWidth - 1, numDepth - 1).i, sides.get(5).get(numLength - 1, numDepth - 1));
        points.set(sides.get(5).get(numLength - 1, numDepth - 1).i, sides.get(5).get(numLength - 1, numDepth - 1));

        for (Side s : sides) {
            faces.addAll(s.faces());
        }

        this.getTexCoords().addAll(0, 0);
        for (Vector3 v : points) {
            this.getPoints().addAll((float) v.x, (float) v.y, (float) v.z);
        }
        for (Face f : faces) {
            this.getFaces().addAll(f.p1, 0, f.p2, 0, f.p3, 0);
        }
    }

    void updatePoints(Vector3 v) {
        this.getPoints().clear();
        Vector3 p;
        for (int i = 0; i < points.size(); i++) {
            p = points.get(i);
            p = p.add(v);
            points.set(i, p);
            this.getPoints().addAll((float) p.x, (float) p.y, (float) p.z);
        }

    }
}
