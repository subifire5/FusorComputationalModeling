/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class Shape extends TriangleMesh {

    float[] vertices = null;
    int[] faces = null;

    //
    // rayIntersect
    //
    // goes through all the triangles in the shape to find the intersection
    //
    double rayIntersect(Vector3D rayOrigin, Vector3D rayDirection) {
        if (vertices == null) {
            vertices = getPoints().toArray(null);
        }

        if (faces == null) {
            faces = getFaces().toArray(null);
        }

        for (int i = 0; i < faces.length; i += 6) {
            Vector3D v0 = new Vector3D(vertices[faces[i]], vertices[faces[i] + 1], vertices[faces[i] + 2]);
            Vector3D v1 = new Vector3D(vertices[faces[i + 2]], vertices[faces[i + 2] + 1], vertices[faces[i + 2] + 2]);
            Vector3D v2 = new Vector3D(vertices[faces[i + 4]], vertices[faces[i + 4] + 1], vertices[faces[i + 4] + 2]);

            // test the front of the face triangle (going into the shape)
            double t = rayTriangleIntersect(rayOrigin, rayDirection, v0, v1, v2);
            if (t != 0) {
                return t;
            }

            // test the backside of the shape (going out)
            t = rayTriangleIntersect(rayOrigin, rayDirection, v0, v2, v1);
            if (t != 0) {
                return t;
            }
        }

        return 0;
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

}
