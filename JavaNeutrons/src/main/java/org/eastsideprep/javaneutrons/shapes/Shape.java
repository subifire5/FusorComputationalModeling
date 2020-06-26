/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.shapes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Transform;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.assemblies.Material;
import org.eastsideprep.javaneutrons.assemblies.Part;
import org.eastsideprep.javaneutrons.core.Util;
import org.fxyz3d.importers.Importer3D;
//import org.j3d.loaders.stl.STLFileReader;

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
    public String name;

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

    // use this constructor to construct a shape from an OBJ/STL file
    // will use only the first mesh in the group
    public Shape(URL url) {
        ArrayList<Shape> shapes;

        if (url.toString().toLowerCase().endsWith("obj")) {
            shapes = loadSTL(url);
        } else if (url.toString().toLowerCase().endsWith("stl")) {
            shapes = loadSTL(url);
        } else {
            throw new IllegalArgumentException("Shape contructor: Not OBJ/STL file: " + url);
        }

        if (shapes.size() != 1) {
            throw new IllegalArgumentException("Contructing shape from OBJ/STL file containing more or fewer than one mesh: " + url);
        }
        this.mesh = (TriangleMesh) shapes.get(0).getMesh();
        super.setMesh(this.mesh);
        setVisuals("green");
    }

    public void setName(String name) {
        this.name = name;
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

    public static Scanner openSTL(URL url) {
        InputStream is;
        try {
            is = new FileInputStream(new File(url.toURI()));
        } catch (Exception e) {
            System.err.println("Error opening STL file " + url + ": " + e);
            return null;
        }
        Scanner sc = new Scanner(is);
        return sc;
    }

    public static String nextObject(Scanner sc) {
        if (!sc.hasNextLine()) {
            return null;
        }
        String object = sc.nextLine();
        if (!object.startsWith("solid ")) {
            return null;
        }
        return object.substring(6);
    }

    public static float[][] nextFacet(Scanner sc) {
        if (!sc.hasNextLine() || !sc.nextLine().trim().startsWith("facet")) {
            return null;
        }

        if (!sc.hasNextLine() || !sc.nextLine().trim().equals("outer loop")) {
            return null;
        }

        float[][] face = new float[3][3];
        for (int i = 0; i < 3; i++) {
            String line = sc.nextLine();
            if (!line.trim().startsWith("vertex")) {
                return null;
            }
            line = line.trim().substring(7);
            String[] numbers = line.split(" ");
            for (int j = 0; j < 3; j++) {
                face[i][j] = Float.valueOf(numbers[j]);
            }
        }

        if (!sc.hasNextLine() || !sc.nextLine().trim().equals("endloop")) {
            return null;
        }

        if (!sc.hasNextLine() || !sc.nextLine().trim().equals("endfacet")) {
            return null;
        }

        return face;
    }

    public static ArrayList<Shape> loadSTL(URL url) {

        Scanner sc = openSTL(url);
        if (sc == null) {
            return null;
        }

        ArrayList<Shape> result = new ArrayList<>();

        String object = nextObject(sc);
        while (object != null) {
            TriangleMesh m = new TriangleMesh();
            HashMap<Object, Integer> vertexMap = new HashMap<>();
            ArrayList<int[]> facesList = new ArrayList<>();

            // read all face data
            float[][] face = nextFacet(sc);
            while (face != null) {
                int[] faceData = new int[6];
                // for each vertex in the current face
                for (int k = 0; k < 3; k++) { // 3 points in a triangle
                    // convert it to float
                    // try to find it
                    int vertex = vertexMap.getOrDefault(face[k], -1);
                    if (vertex == -1) {
                        // if not found, insert
                        vertex = vertexMap.size();
                        vertexMap.put(face[k], vertex);
                    }
                    faceData[2 * k] = vertex;
                    facesList.add(faceData);
                }
                face = nextFacet(sc);
            }
            // prepare mesh
            m.setVertexFormat(VertexFormat.POINT_TEXCOORD);

            // need to convert vertices 
            float[] vertices = new float[vertexMap.size() * 3];
            for (Entry<Object, Integer> e : vertexMap.entrySet()) {
                System.arraycopy(((float[]) e.getKey()), 0, vertices, e.getValue(), 3);
            }
            // ad converted vertices to mesh
            m.getPoints().addAll(vertices);

            // set dummy texcoords
            m.getTexCoords().addAll(0, 0);

            // convert faces to flat array
            int[] faces = new int[facesList.size() * 6];
            for (int j = 0; j < facesList.size(); j++) {
                System.arraycopy(facesList.get(j), 0, faces, j * 6, 6);
            }
            // add faces to mesh
            m.getFaces().addAll(faces);

            Shape s = new Shape(m);
            s.setName(object);
            result.add(s);

            // and next object until done with file
            object = nextObject(sc);
        }

        return result;
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
    public double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection, boolean goingOut, int[] faceIndex, LinkedTransferQueue simVis) {
        double tmin = -1;
        int x = 0;
        int y = 1;
        int z = 2;
        int face = -1;
        if (java.lang.Math.abs(rayDirection.getNorm() - 1) > 1e-8) {
            System.out.println("direction not normalized in rayIntersect");
        }

        double ox = rayOrigin.getX();
        double oy = rayOrigin.getY();
        double oz = rayOrigin.getZ();

        double dx = rayDirection.getX();
        double dy = rayDirection.getY();
        double dz = rayDirection.getZ();

        cacheVerticesAndFaces();
//        System.out.println("Checking part" + this.part.name);
        int vis = this.mesh.getVertexFormat().getVertexIndexSize();

        for (int i = 0; i < faces.length; i += 6) {
//            Vector3D v0 = new Vector3D(vertices[3 * faces[i] + x], vertices[3 * faces[i] + y], vertices[3 * faces[i] + z]);
//            Vector3D v1 = new Vector3D(vertices[3 * faces[i + vis] + x], vertices[3 * faces[i + vis] + y], vertices[3 * faces[i + vis] + z]);
//            Vector3D v2 = new Vector3D(vertices[3 * faces[i + 2 * vis] + x], vertices[3 * faces[i + 2 * vis] + y], vertices[3 * faces[i + 2 * vis] + z]);
//            System.out.println("Checking");
//            System.out.println("" + v0);
//            System.out.println("" + v1);
//            System.out.println("" + v2);

            // goingOut determines whether we test the counter-clockwise triangle (front face)
            // or clockwise triangle (back face). We assume that all faces of a shape face outward. 
//            Vector3D v0 = new Vector3D(vertices[3 * faces[i] + x], vertices[3 * faces[i] + y], vertices[3 * faces[i] + z]);
//            Vector3D v1 = new Vector3D(vertices[3 * faces[i + vis] + x], vertices[3 * faces[i + vis] + y], vertices[3 * faces[i + vis] + z]);
//            Vector3D v2 = new Vector3D(vertices[3 * faces[i + 2 * vis] + x], vertices[3 * faces[i + 2 * vis] + y], vertices[3 * faces[i + 2 * vis] + z]);
//            System.out.println(v0);
//            System.out.println(v1);
//            System.out.println(v2);
//           double t = Util.Math.rayTriangleIntersect(rayOrigin, rayDirection, v0, goingOut ? v2 : v1, goingOut ? v1 : v2);
            double t = Util.Math.rayTriangleIntersect(ox, oy, oz, dx, dy, dz,
                    vertices[3 * faces[i] + x], vertices[3 * faces[i] + y], vertices[3 * faces[i] + z],
                    goingOut ? vertices[3 * faces[i + 2 * vis] + x] : vertices[3 * faces[i + vis] + x],
                    goingOut ? vertices[3 * faces[i + 2 * vis] + y] : vertices[3 * faces[i + vis] + y],
                    goingOut ? vertices[3 * faces[i + 2 * vis] + z] : vertices[3 * faces[i + vis] + z],
                    goingOut ? vertices[3 * faces[i + vis] + x] : vertices[3 * faces[i + 2 * vis] + x],
                    goingOut ? vertices[3 * faces[i + vis] + y] : vertices[3 * faces[i + 2 * vis] + y],
                    goingOut ? vertices[3 * faces[i + vis] + z] : vertices[3 * faces[i + 2 * vis] + z]
            );
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
        if (faces[faceIndex + 1] == 0) {
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
    public void markSurfaceInContactWith(Vector3D origin, Vector3D direction, Material material, LinkedTransferQueue g) {
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
