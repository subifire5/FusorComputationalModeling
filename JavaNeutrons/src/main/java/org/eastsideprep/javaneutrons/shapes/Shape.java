/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.shapes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Transform;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.assemblies.Material;
import org.eastsideprep.javaneutrons.assemblies.Part;
import org.eastsideprep.javaneutrons.core.Util;
import org.fxyz3d.importers.Importer3D;

/**
 *
 * @author gunnar
 */
public class Shape extends MeshView {

    TriangleMesh mesh = null;
    double[] vertices = null;
    int[] faces = null;
    public Part part;
    public Material containedMaterial;

    // fresh
    public Shape() {
        this.mesh = new TriangleMesh();
        super.setMesh(mesh);
        setVisuals("gray");
    }

    // clone
    public Shape(Shape shape) {
        super(shape.mesh);
        this.mesh = shape.mesh;
        setVisuals("yellow");
    }

    // from existing triangle mesh
    public Shape(TriangleMesh mesh) {
        super(mesh);
        this.mesh = mesh;
        setVisuals("purple");
    }

    // use this constructor to construct a shape from an FXyz object 
    // (they extend MeshView)
    public Shape(MeshView mv) {
        Mesh m = mv.getMesh();

        if (m instanceof TriangleMesh) {
            this.mesh = (TriangleMesh) m;
            super.setMesh(m);
        } else {
            throw new IllegalArgumentException("Constructing Shape from invalid kind of mesh: " + m);
        }

        setVisuals("purple");
    }

    // use this constructor to construct a shape from an OBJ file
    // will use only the first mesh in the group
    public Shape(URL url) {
        ArrayList<Shape> shapes = loadOBJ(url);
        if (shapes.size() != 1) {
            throw new IllegalArgumentException("Contructing shape from OBJ file containing more or fewer than one mesh: " + url);
        }
        this.mesh = (TriangleMesh) shapes.get(0).getMesh();
        super.setMesh(this.mesh);
        setVisuals("green");
    }

    //
    // setVisuals
    // default vis attributes for shapes
    //
    private void setVisuals(String color) {
        this.setColor(color);
        this.setOpacity(0.5);
        this.setDrawMode(DrawMode.LINE);
    }

    //
    // loadOBJ
    //
    // static helper, can be used to load multi-shape OBJ files
    //
    public static ArrayList<Shape> loadOBJ(URL url) {
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
                //System.out.println("Adding shape");
                shapes.add(new Shape((MeshView) n));
            }
        }
        return shapes;
    }

    private double[] getVertices() {
        float[] floats = mesh.getPoints().toArray(null);
        double[] v = new double[floats.length];
        for (int i = 0; i < floats.length; i++) {
            v[i] = floats[i];
        }
        return v;
    }

    private double[] getTransformedVertices() {
        double[] v = getVertices();

        // transform all vertices by all transforrms
        double[] verticesTransformed = new double[v.length];
        for (int i = this.getTransforms().size() - 1; i >= 0; i--) {
            Transform t = this.getTransforms().get(i);
            t.transform3DPoints(v, 0,
                    verticesTransformed, 0,
                    v.length / 3);
            double[] temp = v;
            v = verticesTransformed;
            verticesTransformed = temp;
        }
        return v;
    }

    private int[] getFaces() {
        return mesh.getFaces().toArray(null);
    }

    private void cacheVerticesAndFaces() {
        synchronized (this) {
            if (vertices == null) {
                //System.out.println("Original vertices for " + this.part.name);
                //System.out.println(Arrays.toString(this.getVertices()));
                vertices = getTransformedVertices();
                //System.out.println("Vertices for " + this.part.name);
                //System.out.println(Arrays.toString(vertices));
            }

            if (faces == null) {
                faces = getFaces();
                //System.out.println("Faces for " + this.part.name);
                //System.out.println(Arrays.toString(faces));
            }
        }
    }

    //
    // rayIntersect
    //
    // goes through all the triangles in the shape to find the intersection
    // returns t-parameter for the ray, or 0 if not intersecting
    // todo: acceleration structure like hierarchy of volumes
    //
    public double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut, int[] faceIndex, Group simVis) {
        double tmin = -1;
        int x = 0;
        int y = 1;
        int z = 2;
        int face = -1;
        if (java.lang.Math.abs(rayDirection.getNorm() - 1) > 1e-8) {
            System.out.println("direction not normalized in rayIntersect");
        }

        cacheVerticesAndFaces();
//        System.out.println("Checking part" + this.part.name);
        int vis = this.mesh.getVertexFormat().getVertexIndexSize();

        for (int i = 0; i < faces.length; i += 6) {
            Vector3D v0 = new Vector3D(vertices[3 * faces[i] + x], vertices[3 * faces[i] + y], vertices[3 * faces[i] + z]);
            Vector3D v1 = new Vector3D(vertices[3 * faces[i + vis] + x], vertices[3 * faces[i + vis] + y], vertices[3 * faces[i + vis] + z]);
            Vector3D v2 = new Vector3D(vertices[3 * faces[i + 2 * vis] + x], vertices[3 * faces[i + 2 * vis] + y], vertices[3 * faces[i + 2 * vis] + z]);
//            System.out.println("Checking");
//            System.out.println("" + v0);
//            System.out.println("" + v1);
//            System.out.println("" + v2);

            // goingOut determines whether we test the counter-clockwise triangle (front face)
            // or clockwise triangle (back face). We assume that all faces of a shape face outward. 
            double t = Util.Math.rayTriangleIntersect(rayOrigin, rayDirection, v0, goingOut ? v2 : v1, goingOut ? v1 : v2);
            if (t != -1) {
//                System.out.println("found triangle " + v0);

                if (tmin != -1) {
                    if (t < tmin) {
                        tmin = t;
                        face = i;
                    }
                } else {
                    tmin = t;
                    face = i;
                }
            }

        }
//        if (face != -1) {
//            Vector3D v0 = new Vector3D(vertices[3 * faces[face] + x], vertices[3 * faces[face] + y], vertices[3 * faces[face] + z]);
//            Vector3D v1 = new Vector3D(vertices[3 * faces[face + 2] + x], vertices[3 * faces[face + 2] + y], vertices[3 * faces[face + 2] + z]);
//            Vector3D v2 = new Vector3D(vertices[3 * faces[face + 4] + x], vertices[3 * faces[face + 4] + y], vertices[3 * faces[face + 4] + z]);
//
//            System.out.println("" + v0);
//            System.out.println("" + v1);
//            System.out.println("" + v2);
//            Util.Graphics.drawLine(simVis, v0, v1, Color.GREEN);
//            Util.Graphics.drawLine(simVis, v1, v2, Color.GREEN);
//            Util.Graphics.drawLine(simVis, v2, v0, Color.GREEN);
//            Util.Graphics.drawSphere(simVis, Util.Math.rayPoint(rayOrigin, rayDirection, tmin), vis, "orange");
//        }

        // report back the face index if asked for
        if (faceIndex != null) {
            faceIndex[0] = face;
        }

        return tmin;
    }

    public Material getContactMaterial(int faceIndex) {
        if (faces[faceIndex+1] == 0) {
            return null;
        }
        return this.containedMaterial;
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

        double[] v = vertices;

        for (int i = 0; i < faces.length; i += 6) {
            Vector3D v0 = new Vector3D(v[3 * faces[i]], v[3 * faces[i] + 1], v[3 * faces[i] + 2]);
            Vector3D v1 = new Vector3D(v[3 * faces[i + 2]], v[3 * faces[i + 2] + 1], v[3 * faces[i + 2] + 2]);
            Vector3D v2 = new Vector3D(v[3 * faces[i + 4]], v[3 * faces[i + 4] + 1], v[3 * faces[i + 4] + 2]);

            v0 = v0.crossProduct(v1);
            volume += v0.dotProduct(v2);
        }

        return Math.abs(volume / 6.0);
    }

    //
    // getExtent
    //
    // calculates the max bondaries of a mesh in O(N)
    //
    public Vector3D getExtent() {
        cacheVerticesAndFaces();

        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < vertices.length; i += 3) {
            xmin = Math.min(vertices[i], xmin);
            xmax = Math.max(vertices[i], xmax);
            ymin = Math.min(vertices[i + 1], ymin);
            ymax = Math.max(vertices[i + 1], ymax);
            zmin = Math.min(vertices[i + 2], zmin);
            zmax = Math.max(vertices[i + 2], zmax);
        }

        return new Vector3D(xmax - xmin, ymax - ymin, zmax - zmin);
    }

    //
    // setColor
    //
    public void setColor(String webColor) {
        final PhongMaterial pm = new PhongMaterial();
        pm.setSpecularColor(Color.web(webColor));
        pm.setDiffuseColor(Color.web(webColor));
        this.setMaterial(pm);
    }

    // 
    // for containing things other than air
    //
    public void markSurfaceInContactWith(Vector3D origin, Vector3D direction, Material material, Group g) {
        // find the triangle face
        int[] face = new int[1];
        this.rayIntersect(origin, direction, false, face, g);

        // set up a queue of triangles that share the points of this one
        LinkedList<Integer> facesQueue = new LinkedList<>();

        // and a set of processed faces so we don't visit one twice
        HashSet<Integer> facesAdded = new HashSet<>();

        // let's get started
        facesQueue.add(face[0]);
        facesAdded.add(0);

        while (!facesQueue.isEmpty()) {
            // get a face to process
            int nextFace = facesQueue.removeFirst();

            // mark it as "special"
            faces[nextFace + 1] = 1; // regularly 0

            // queue all faces that share points with this one
            addFacesForPoint(faces[nextFace], facesQueue, facesAdded);
            addFacesForPoint(faces[nextFace + 2], facesQueue, facesAdded);
            addFacesForPoint(faces[nextFace + 4], facesQueue, facesAdded);
        }

        this.containedMaterial = material;
    }

    private void addFacesForPoint(int pointIndex, LinkedList<Integer> facesQueue, HashSet<Integer> facesAdded) {
        for (int i = 0; i < faces.length; i += 6) {
            if (faces[i] == pointIndex || faces[i + 2] == pointIndex || faces[i + 4] == pointIndex) {
                if (!facesAdded.contains(i)) {
                    facesQueue.add(i);
                    facesAdded.add(i);
                }
            }
        }
    }
}
