/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.fxyz3d.importers.Importer3D;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

/**
 *
 * @author gunnar
 */
public class Shape extends MeshView {

    TriangleMesh mesh = null;
    float[] vertices = null;
    int[] faces = null;
    Part part;

    // fresh
    Shape() {
        this.mesh = new TriangleMesh();
        super.setMesh(mesh);
    }

    // clone
    Shape(Shape shape) {
        super(shape.mesh);
        this.mesh = shape.mesh;
    }

    // from existing triangle mesh
    Shape(TriangleMesh mesh) {
        super(mesh);
        this.mesh = mesh;
    }

    // use this constructor to construct a shape from an FXyz object 
    // (they extend MeshView)
    Shape(MeshView mv) {
        Mesh m = mv.getMesh();

        if (m instanceof TriangleMesh) {
            this.mesh = (TriangleMesh) m;
            super.setMesh(m);
        } else {
            throw new IllegalArgumentException("Constructing Shape from invalid kind of mesh: " + m);
        }
    }

    // use this constructor to construct a shape from an OBJ file
    // will use only the first mesh in the group
    Shape(URL url) {
        ArrayList<Shape> shapes = loadOBJ(url);
        if (shapes.size() != 1) {
            throw new IllegalArgumentException("Contructing shape from OBJ file containing more or fewer than one mesh: " + url);
        }
        this.mesh = (TriangleMesh) shapes.get(0).getMesh();
        super.setMesh(this.mesh);
    }

    //
    // loadOBJ
    //
    // static helper, can be used to load multi-shape OBJ files
    //
    static ArrayList<Shape> loadOBJ(URL url) {
        ArrayList<Shape> shapes = new ArrayList<>();

        Group g = null;
        try {
            g = Importer3D.load(url).getRoot();
        } catch (IOException e) {
            System.err.println("Error reading OBJ file " + url);
        }

        // go through the group of objects
        // add all triangle meshes to the list
        for (Node n : g.getChildren()) {
            if (n instanceof MeshView) {
                shapes.add(new Shape((MeshView) n));
            }
        }
        return shapes;
    }

    private void cacheVerticesAndFaces() {
        if (vertices == null) {
            vertices = mesh.getPoints().toArray(null);
        }

        if (faces == null) {
            faces = mesh.getFaces().toArray(null);
        }
    }

    //
    // rayIntersect
    //
    // goes through all the triangles in the shape to find the intersection
    // returns t-parameter for the ray, or 0 if not intersecting
    // todo: acceleration structure like hierarchy of volumes
    //
    double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut) {
        double tmin = 0;

        cacheVerticesAndFaces();

        for (int i = 0; i < faces.length; i += 6) {
            Vector3D v0 = new Vector3D(vertices[faces[i]], vertices[faces[i] + 1], vertices[faces[i] + 2]);
            Vector3D v1 = new Vector3D(vertices[faces[i + 2]], vertices[faces[i + 2] + 1], vertices[faces[i + 2] + 2]);
            Vector3D v2 = new Vector3D(vertices[faces[i + 4]], vertices[faces[i + 4] + 1], vertices[faces[i + 4] + 2]);

            // goingOut determines whether we test the counter-clockwise triangle (front face)
            // or clockwise triangle (back face). We assume that all faces of a shape face outward. 
            double t = rayTriangleIntersect(rayOrigin, rayDirection, v0, goingOut ? v2 : v1, goingOut ? v1 : v2);
            if (t != 0) {
                if (tmin != 0) {
                    tmin = Math.min(tmin, t);
                } else {
                    tmin = t;
                }
            }
        }

        return tmin;
    }

    //
    // rayTriangleIntersect
    //
    // static helper function
    // ported from: 
    // https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution
    //
    public static double rayTriangleIntersect(
            Vector3D rayOrigin, Vector3D rayDirection,
            Vector3D v0, Vector3D v1, Vector3D v2) {
        final double kEpsilon = 1E-8; // constant for "close enough to 0"

        // compute plane's normal
        Vector3D v0v1 = v1.subtract(v0);
        Vector3D v0v2 = v2.subtract(v0);
        // no need to normalize
        Vector3D N = v0v1.crossProduct(v0v2); // N 
        double area2 = N.getNorm();

        // Step 1: finding P
        // check if ray and plane are parallel ?
        double NdotRayDirection = N.dotProduct(rayDirection);

        // almost 0?
        if (Math.abs(NdotRayDirection) < kEpsilon) {
            return 0; // they are parallel so they don't intersect ! 
        }

        // compute d parameter using equation 2
        double d = N.dotProduct(v0);

        // compute t (equation 3)
        double t = (N.dotProduct(rayOrigin) + d) / NdotRayDirection;
        // check if the triangle is in behind the ray
        if (t < 0) {
            return 0; // the triangle is behind 
        }
        // compute the intersection point using equation 1
        Vector3D P = rayOrigin.add(rayDirection.scalarMultiply(t));

        // Step 2: inside-outside test
        Vector3D C; // vector perpendicular to triangle's plane 

        // edge 0
        Vector3D edge0 = v1.subtract(v0);
        Vector3D vp0 = P.subtract(v0);
        C = edge0.crossProduct(vp0);
        if (N.dotProduct(C) < 0) {
            return 0; // P is on the right side 
        }

        // edge 1
        Vector3D edge1 = v2.subtract(v1);
        Vector3D vp1 = P.subtract(v1);
        C = edge1.crossProduct(vp1);
        if (N.dotProduct(C) < 0) {
            return 0; // P is on the right side 
        }
        // edge 2
        Vector3D edge2 = v0.subtract(v2);
        Vector3D vp2 = P.subtract(v2);
        C = edge2.crossProduct(vp2);
        if (N.dotProduct(C) < 0) {
            return 0; // P is on the right side; 
        }
        return t; // this ray hits the triangle, return where on the ray
    }

    
    //
    // getVolume
    //
    // calculates the volume of a mesh in O(N)
    // todo: since the math is so simple, this could be done without creating objects
    //
    public double getVolume() {
        double volume = 0;

        cacheVerticesAndFaces();
        
        for (int i = 0; i < faces.length; i += 6) {
            Vector3D v0 = new Vector3D(vertices[faces[i]], vertices[faces[i] + 1], vertices[faces[i] + 2]);
            Vector3D v1 = new Vector3D(vertices[faces[i + 2]], vertices[faces[i + 2] + 1], vertices[faces[i + 2] + 2]);
            Vector3D v2 = new Vector3D(vertices[faces[i + 4]], vertices[faces[i + 4] + 1], vertices[faces[i + 4] + 2]);

            v0 = v0.crossProduct(v1);
            volume += v0.dotProduct(v2);
        }

        return Math.abs(volume / 6.0);
    }
}
