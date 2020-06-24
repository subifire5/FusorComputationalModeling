/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author gmein
 */
public class Util {

    static public class Math {

        public static Random random = new Random();

        public static Vector3D randomDir() {
            double phi = Util.Math.random.nextDouble() * 2 * java.lang.Math.PI;
            double z = Util.Math.random.nextDouble() * 2 - 1;
            double theta = java.lang.Math.asin(z);
            return new Vector3D(java.lang.Math.cos(theta) * java.lang.Math.cos(phi), java.lang.Math.cos(theta) * java.lang.Math.sin(phi), z);
        }

        //
        // similar
        //
        public static Vector3D randomGaussianComponentVector(double sd) {
            return new Vector3D(random.nextGaussian() * sd, random.nextGaussian() * sd, random.nextGaussian() * sd);
        }

        //
        // rayTriangleIntersect
        //
        // static helper function
        // ported from: 
        // https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution
        //
// public static double rayTriangleIntersect(
//                Vector3D orig, Vector3D dir,
//                Vector3D v0, Vector3D v1, Vector3D v2) {
        public static double rayTriangleIntersectOld(
                Vector3D orig, Vector3D dir,
                double v0x, double v0y, double v0z,
                double v1x, double v1y, double v1z,
                double v2x, double v2y, double v2z) {

            final double kEpsilon = 1E-8; // constant for "close enough to 0"

            Vector3D v0 = new Vector3D(v0x, v0y, v0z);
            Vector3D v1 = new Vector3D(v1x, v1y, v1z);
            Vector3D v2 = new Vector3D(v2x, v2y, v2z);

//            System.out.println(v0);
//            System.out.println(v1);
//            System.out.println(v2);
            // compute plane's normal
            Vector3D v0v1 = v1.subtract(v0);
            Vector3D v0v2 = v2.subtract(v0);
            // no need to normalize
            Vector3D N = v0v1.crossProduct(v0v2); // N 
            double area2 = N.getNorm();

            // Step 1: finding P
            // check if ray and plane are parallel ?
            double nDotDir = N.dotProduct(dir);

            // almost 0?
            if (nDotDir > -kEpsilon) {
                return -1; // wrong direction or parallel 
            }

            // compute d parameter using equation 2
            double d = N.dotProduct(v0);

            // compute t (equation 3)
            double t = (d - N.dotProduct(orig)) / nDotDir;
            // check if the triangle is in behind the ray
            if (t < 0) {
                return -1; // the triangle is behind 
            }
            // compute the intersection point using equation 1
            Vector3D P = orig.add(dir.scalarMultiply(t));

            // Step 2: inside-outside test
            Vector3D C; // vector perpendicular to triangle's plane 

            // edge 0
            Vector3D edge0 = v1.subtract(v0);
            Vector3D vp0 = P.subtract(v0);
            C = edge0.crossProduct(vp0);
            if (N.dotProduct(C) < 0) {
                return -1; // P is on the right side 
            }

            // edge 1
            Vector3D edge1 = v2.subtract(v1);
            Vector3D vp1 = P.subtract(v1);
            C = edge1.crossProduct(vp1);
            if (N.dotProduct(C) < 0) {
                return -1; // P is on the right side 
            }
            // edge 2
            Vector3D edge2 = v0.subtract(v2);
            Vector3D vp2 = P.subtract(v2);
            C = edge2.crossProduct(vp2);
            if (N.dotProduct(C) < 0) {
                return -1; // P is on the right side; 
            }

            return t; // this ray hits the triangle, return where on the ray
        }

        //
        // rayTriangleIntersectNew
        //
        // static helper function
        // adapted from: 
        // https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm#Java_Implementation
        //
        public static double rayTriangleIntersectOld2(
                Vector3D orig, Vector3D dir,
                double v0x, double v0y, double v0z,
                double v1x, double v1y, double v1z,
                double v2x, double v2y, double v2z) {
            final double kEpsilon = 1E-12; // constant for "close enough to 0"
            double a, f, u, v;

            Vector3D v0 = new Vector3D(v0x, v0y, v0z);
            Vector3D v1 = new Vector3D(v1x, v1y, v1z);
            Vector3D v2 = new Vector3D(v2x, v2y, v2z);

            Vector3D edge1 = v1.subtract(v0);
            System.out.println("Edge1" + edge1);

            Vector3D edge2 = v2.subtract(v0);
            System.out.println("Edge2" + edge2);
            Vector3D h = dir.crossProduct(edge2);
            System.out.println("h" + h);
            a = edge1.dotProduct(h);
            if (a > -kEpsilon && a < kEpsilon) {
                return -1;    // This ray is parallel to this triangle.
            }

            Vector3D n = edge1.crossProduct(edge2);
            System.out.println("n" + n);
            double nDotDir = n.dotProduct(dir);

            // addition: need to check her whether the ray is goin in the wrong direction
            if (nDotDir > -kEpsilon) {
                return -1; // wrong direction or parallel 
            }

            f = 1.0 / a;
            System.out.println("f " + f);

            Vector3D s = orig.subtract(v0);
            System.out.println("s" + s);
            u = f * (s.dotProduct(h));
            System.out.println("u " + u);
            if (u < 0.0 || u > 1.0) {
                return -1;
            }
            Vector3D q = s.crossProduct(edge1);
            System.out.println("q" + q);
            v = f * dir.dotProduct(q);
            System.out.println("v " + v);

            if (v < 0.0 || u + v > 1.0) {
                return -1;
            }
            // At this stage we can compute t to find out where the intersection point is on the line.

            double t = f * edge2.dotProduct(q);
            System.out.println("t " + t);

            if (t > kEpsilon) {
                return t;
            } else {
                return -1;
            }
        }

        public static double rayTriangleIntersect(
                Vector3D orig, Vector3D dir,
                double v0x, double v0y, double v0z,
                double v1x, double v1y, double v1z,
                double v2x, double v2y, double v2z) {
            //System.out.println("");

            //rayTriangleIntersectNew(orig, dir, v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z);

            final double kEpsilon = 1E-12; // constant for "close enough to 0"
            double a, f, u, v;

//            Vector3D v0 = new Vector3D(v0x, v0y, v0z);
//            Vector3D v1 = new Vector3D(v1x, v1y, v1z);
//            Vector3D v2 = new Vector3D(v2x, v2y, v2z);

            double edge1x = v1x - v0x;
            double edge1y = v1y - v0y;
            double edge1z = v1z - v0z;

            //Vector3D edge1 = v1.subtract(v0);
            //System.out.println("Edge1" + edge1);

            double edge2x = v2x - v0x;
            double edge2y = v2y - v0y;
            double edge2z = v2z - v0z;

            //Vector3D edge2 = v2.subtract(v0);
            //System.out.println("Edge2" + edge2);

            double hx = dir.getY() * edge2z - dir.getZ() * edge2y;
            double hy = dir.getZ() * edge2x - dir.getX() * edge2z;
            double hz = dir.getX() * edge2y - dir.getY() * edge2x;

            //Vector3D h = dir.crossProduct(edge2);
            //System.out.println("h" + h);

            a = edge1x * hx + edge1y * hy + edge1z * hz;

            //a = edge1.dotProduct(h);

            if (a > -kEpsilon && a < kEpsilon) {
                return -1;    // This ray is parallel to this triangle.
            }

            //
            double nx = edge1y * edge2z - edge1z * edge2y;
            double ny = edge1z * edge2x - edge1x * edge2z;
            double nz = edge1x * edge2y - edge1y * edge2x;

            //Vector3D n = edge1.crossProduct(edge2);
            //System.out.println("n" + n);

            double nDotDir = nx * dir.getX()
                    + ny * dir.getY() + nz * dir.getZ();

            //nDotDir = n.dotProduct(dir);

            // addition: need to check her whether the ray is goin in the wrong direction
            if (nDotDir > -kEpsilon) {
                return -1; // wrong direction or parallel 
            }

            f = 1.0 / a;
            //System.out.println("f " + f);

            double sx = orig.getX() - v0x;
            double sy = orig.getY() - v0y;
            double sz = orig.getZ() - v0z;

            //Vector3D s = orig.subtract(v0);
            //System.out.println("s" + s);
            u = f * (sx * hx + sy * hy + sz * hz);

            //u = f * s.dotProduct(h);
            //System.out.println("u " + u);

            if (u < 0.0 || u > 1.0) {
                return -1;
            }

            double qx = sy * edge1z - sz * edge1y;
            double qy = sz * edge1x - sx * edge1z;
            double qz = sx * edge1y - sy * edge1x;
            //Vector3D q = s.crossProduct(edge1);
            //System.out.println("q" + q);

            v = f * (dir.getX() * qx + dir.getY() * qy + dir.getZ() * qz);
            //v = f * dir.dotProduct(q);
            //System.out.println("v " + v);

            if (v < 0.0 || u + v > 1.0) {
                return -1;
            }
            // At this stage we can compute t to find out where the intersection point is on the line.
            double t = f * (edge2x * qx + edge2y * qy + edge2z * qz);
            //double t = f * edge2.dotProduct(q);
            //System.out.println("t " + t);

            if (t > kEpsilon) {
                return t;
            } else {
                return -1;
            }
        }

        public static Vector3D rayPoint(Vector3D origin, Vector3D direction, double t) {
            return origin.add(direction.scalarMultiply(t));
        }

    }

    static public class Physics {

        //final public static double boltzmann = 8.61733333353e-5; //eV/K
        final public static double boltzmann = 1.38064852e-23; // SI with cm
        final public static double roomTemp = 293.0; // K
        final public static double protonMass = 1.67262192369e-27; // SI
        final public static double eV = 1.60218e-19 * 1e4; // 1 eV in SI with cm
        final public static double barn = 1e-24; // 1 barn in SI cm
        // factor 1e4 is from using cm, not m here - 100^2
    }

    static public class Graphics {

        public static void drawSphere(LinkedTransferQueue<Node> g, Vector3D position, float radius, String webColor) {
            Sphere s = new Sphere(radius);
            s.setTranslateX(position.getX());
            s.setTranslateY(position.getY());
            s.setTranslateZ(position.getZ());
            final PhongMaterial pm = new PhongMaterial();
            pm.setSpecularColor(Color.web(webColor));
            pm.setDiffuseColor(Color.web(webColor));
            s.setMaterial(pm);

            g.add(s);
        }

        public static void drawLine(LinkedTransferQueue<Node> g, Vector3D p1, Vector3D p2, double size, Color c) {
            Vector3D v = p2.subtract(p1);

            Cylinder line = new Cylinder(size, v.getNorm(), 4);
            PhongMaterial pm = new PhongMaterial(c);
            line.setMaterial(pm);

            double phi = java.lang.Math.atan2(v.getX(), v.getY()) * 180 / java.lang.Math.PI;
            double theta = java.lang.Math.acos(v.getZ() / v.getNorm()) * 180 / java.lang.Math.PI;

            line.getTransforms().add(new Translate((p2.getX() + p1.getX()) / 2,
                    (p2.getY() + p1.getY()) / 2, (p2.getZ() + p1.getZ()) / 2));
            line.getTransforms().add(new Rotate(180 - phi, new Point3D(0, 0, 1)));
            line.getTransforms().add(new Rotate(theta - 90, new Point3D(1, 0, 0)));

            g.add(line);
        }

        //
        // visualizeEvent
        //
        // will make a small golden sphere at the event point
        //
        public static void visualizeEvent(Event event, Vector3D direction, LinkedTransferQueue<Node> g) {
            if (event.code != Event.Code.Gone) {
                String color;
                float size = 0.2f;

                switch (event.code) {
                    case Entry:
                        color = "green";
                        size *= 2;
                        break;
                    case Exit:
                        color = "red";
                        size *= 2;
                        break;
                    case Scatter:
                        color = "gold";
                        break;
                    case EmergencyExit:
                        color = "purple";
                        size *= 5;
                        break;
                    case Capture:
                        color = "lightblue";
                        size *= 3;
                        break;
                    default:
                        color = "black";
                        break;
                }
                Vector3D position = event.position;
                if (direction != null) {
                    double jitter = 0.1;
                    position = position.add(direction.scalarMultiply(-jitter));
                }
                Util.Graphics.drawSphere(g, position, size, color);
                //System.out.println("Visualizing "+event.code+" event at " + event.position);
            }
        }

        //
        // visualizeEvent
        //
        // same but without offset direction
        //
        public static void visualizeEvent(Event event, LinkedTransferQueue<Node> g) {
            visualizeEvent(event, null, g);
        }

        public static void drawCoordSystem(LinkedTransferQueue<Node> g) {
            Util.Graphics.drawSphere(g, Vector3D.ZERO, 1, "red");
            Util.Graphics.drawLine(g, new Vector3D(-1000, 0, 0), new Vector3D(1000, 0, 0), 0.1, Color.CYAN);
            Util.Graphics.drawLine(g, new Vector3D(0, -1000, 0), new Vector3D(0, 1000, 0), 0.1, Color.YELLOW);
            Util.Graphics.drawLine(g, new Vector3D(0, 0, -1000), new Vector3D(0, 0, 1000), 0.1, Color.RED);
        }

    }
}
