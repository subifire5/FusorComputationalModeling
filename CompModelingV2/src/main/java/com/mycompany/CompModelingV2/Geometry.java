/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.CompModelingV2;

/**
 *
 * @author subif
 */
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javafx.scene.shape.TriangleMesh;

public class Geometry {

    public List<Triangle> positiveTriangles;
    public List<Triangle> negativeTriangles;
    public List<Triangle> triangles = new ArrayList();
    TriangleMesh positiveMesh;
    TriangleMesh negativeMesh;
    List<Double> positiveSumSA;
    List<Double> negativeSumSA;
    List<Double> triangleSumSA;
    public Double positiveSA;
    public Double negativeSA;
    public Double totalSA;

    public Geometry(String positiveStl, Double positiveCharge, String negativeStl, Double negativeCharge) {
        positiveMesh = importObject(positiveStl);
        negativeMesh = importObject(negativeStl);
        positiveTriangles = getTriangles(positiveMesh, 1);
        negativeTriangles = getTriangles(negativeMesh, -1);
        triangleSumSA = new ArrayList();
        positiveSumSA = new ArrayList();
        negativeSumSA = new ArrayList();
    }


    public TriangleMesh importObject(String fileName) {
        Path path = Paths.get(fileName);
        StlMeshImporter meshImporter = new StlMeshImporter();
        File file = path.toFile();
        meshImporter.read(file);
        TriangleMesh mesh = meshImporter.getImport();
        meshImporter.close();
        return mesh;
    }

    public List<Triangle> getTriangles(TriangleMesh tMesh, int polarity) {

        // .getfaces/.getPoints/whatever all return Observable(Object)Arrays
        // to convert them into normal arrays
        // you pass them an array
        // then they return an array for you to use
        float[] points = null;
        points = tMesh.getPoints().toArray(points);
        int[] faceIndeces = null;
        List<Triangle> ts = new ArrayList();
        faceIndeces = tMesh.getFaces().toArray(faceIndeces);
        System.out.println("Points mod 3: " + points.length % 3);
        System.out.println("Faces mod 6: " + faceIndeces.length % 6);
        System.out.println("Triangle Mesh Vertex Format: " + tMesh.getVertexFormat());
        System.out.println("point size: " + points.length);

        /*
        The Triangle Mesh Vertex Format is POINT_TEXCOORD in this case
        .getFaces returns an array of indeces into arrays
        .getPoints returns an array of floats, each one representing the x, y, or z of a point
        the array looks like this:
        [x1, y1, z1, x2, y2, z2,...]
        the .getFaces, however, is completely different
        what the indexes of .getFaces returns depends on the Vertex Format of your triangle mesh
        
        For example, the faces with VertexFormat.POINT_TEXCOORD that represent a single textured rectangle, using 2 triangles, have the following data order: [ 
        p0, t0, p1, t1, p3, t3, // First triangle of a textured rectangle 
        p1, t1, p2, t2, p3, t3 // Second triangle of a textured rectangle 
        ]
        where p0, p1, p2 and p3 are indices into the points array, n0, n1, n2 and
        n3 are indices into the normals array, and t0, t1, t2 and t3 are indices
        into the texCoords array. 
        
        so if you want a triangle, and our triangle meshes are in TEXCOORD,
        you move in sets of 6
        you get the first item in the faces array
        then use that index in the point array for x
        to get the y, you add one ot that index
        and z you add two
        then you get the third item in the faces array
        then the fifth item in the faces array;
        

        // this means poitns mod 9 should return 
         */
        for (int i = 0; i < faceIndeces.length; i += 6) {
            // three values make up a vector
            // three vectors (9 values) for a triangle
            Vector A = new Vector((double) points[faceIndeces[i] * 3], (double) points[faceIndeces[i] * 3 + 1], (double) points[faceIndeces[i] * 3 + 2]);
            Vector B = new Vector((double) points[faceIndeces[i + 2] * 3], (double) points[faceIndeces[i + 2] * 3 + 1], (double) points[faceIndeces[i + 2] * 3 + 2]);
            Vector C = new Vector((double) points[faceIndeces[i + 4] * 3], (double) points[faceIndeces[i + 4] * 3 + 1], (double) points[faceIndeces[i + 4] * 3 + 2]);

            Triangle t = new Triangle(A, B, C, polarity);
            triangles.add(t);
            ts.add(t);

        }
        return ts;
    }

    public void translatePositiveTriangles(Vector offset) {
        for (Triangle t : triangles) {
            if (t.polarity > 0) {
                t.translate(offset);
            }
        }
    }

    public void translateNegativeTriangles(Vector offset) {
        for (Triangle t : triangles) {
            if (t.polarity < 0) {
                t.translate(offset);
            }
        }
    }

    public void translateTriangles(Vector offset) {
        for (Triangle t : triangles) {
            t.translate(offset);
        }
    }

    // for each polarity
    // make two lists
    // one with triangles, which you add to for each triangle of that polarity
    // another with the total surface area up to that point
    // so if you binary search for a surface area at reandom
    // your chance of gettign any triangle is proportional to that triangle's surface area
    public void sumUpSurfaceArea() {
        positiveSA = 0.0;
        negativeSA = 0.0;
        for (int i = 0; i < triangles.size(); i++) {
            if (triangles.get(i).polarity > 0) {
                positiveSA += triangles.get(i).surfaceArea;
                positiveTriangles.add(triangles.get(i));
                positiveSumSA.add(positiveSA);

            } else if (triangles.get(i).polarity < 0) {
                negativeSA += triangles.get(i).surfaceArea;
                negativeTriangles.add(triangles.get(i));
                negativeSumSA.add(negativeSA);
            }
            triangleSumSA.add(triangles.get(i).surfaceArea);
        }
        Double previous = positiveSumSA.get(0);
        Double current;
        for (int i = 1; i < positiveSumSA.size(); i++) {

            current = positiveSumSA.get(i);
            //System.out.println("current: " + current);
            if (previous > current) {
                System.out.println("oijfodsijf;ldskf");
                break;
            } else {
                previous = current;
            }
        }
        totalSA = positiveSA + negativeSA;
        /*for(Triangle t: triangles){
            System.out.print(t);
            System.out.println("SA: " + t.getSurfaceArea());
        }
         */
        Collections.sort(positiveSumSA);
        System.out.println("collections binary search: " + Collections.binarySearch(positiveSumSA, 5.0));
        System.out.println("my binary search: " + binarySearch(positiveSumSA, 5.0));
    }

    public int binarySearch(List<Double> d, Double target) {
        Boolean first = true;
        Boolean done = false;
        int leftEdge = 0;
        int rightEdge = d.size() - 1;
        int middle = (rightEdge + leftEdge) / 2;

        while (!done) {
            if (middle == rightEdge) {
                if (first) {
                    first = false;
                    if (d.get(leftEdge) > target) {
                        return rightEdge;
                    }
                }
                done = true;
                return rightEdge;
            } else if (middle == leftEdge) {
                //System.out.println("left edge");
                if (first) {
                    first = false;
                    if (d.get(leftEdge) < target) {
                        return rightEdge;
                    }
                }
                done = true;
                return leftEdge;
            }
            Double m = d.get(middle);
            if (m > target) {
                rightEdge = middle;
            } else if (m < target) {
                leftEdge = middle;
            } else {
                done = true;
                return middle;
            }
            if (leftEdge + 1 == rightEdge) {
                if (target > d.get(leftEdge)) {
                    middle = rightEdge;
                } else {
                    middle = leftEdge;
                }
            } else {
                middle = (rightEdge + leftEdge) / 2;
            }

            first = false;
        }
        //System.out.println("ahoaho");
        return -1;
    }

    public Charge getRandomPositiveCharge() {
        Random SAGen = new Random();
        double SA;
        int index;
        Triangle t;
        SA = SAGen.nextDouble();
        //System.out.println("SA: " + SA);
        SA *= positiveSA;
        index = binarySearch(positiveSumSA, SA);

        t = positiveTriangles.get(index);

        return t.genRandCharge();

    }

    public Charge getRandomNegativeCharge() {
        Random SAGen = new Random();
        double SA;
        int index;
        Triangle t;
        SA = SAGen.nextDouble();
        SA *= negativeSA;
        index = binarySearch(negativeSumSA, SA);
        t = negativeTriangles.get(index);
        return t.genRandCharge();
    }

    public Charge getRandomCharge() {
        Random SAGen = new Random();
        int index;
        double SA = totalSA;
        Triangle t;
        SA *= SAGen.nextDouble();
        index = binarySearch(triangleSumSA, SA);
        t = triangles.get(index);
        return t.genRandCharge();
    }

}
