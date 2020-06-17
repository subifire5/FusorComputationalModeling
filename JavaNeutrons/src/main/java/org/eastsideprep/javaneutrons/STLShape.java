/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gunnar
 */
public class STLShape {
    // todo: how to put triangle meshes in here
    // todo: are these always solid objects or can this be a surface/other shape

    //
    // ported from: 
    // https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution
    //
    public static double rayTriangleIntersect(
            Vector3D rayOrigin, Vector3D rayDirection,
            Vector3D v0, Vector3D v1, Vector3D v2,
            double t) {
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
        t = (N.dotProduct(rayOrigin) + d) / NdotRayDirection;
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
