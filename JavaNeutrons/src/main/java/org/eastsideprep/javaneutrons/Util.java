/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.Random;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
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

        static Random random = new Random();

        public static Vector3D randomDir() {
            double phi = Util.Math.random.nextDouble() * 2 * java.lang.Math.PI;
            double z = Util.Math.random.nextDouble() * 2 - 1;
            double theta = java.lang.Math.asin(z);
            return new Vector3D(java.lang.Math.cos(theta) * java.lang.Math.cos(phi), java.lang.Math.cos(theta) * java.lang.Math.sin(phi), z);
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
            if (NdotRayDirection > -kEpsilon) {
                return -1; // wrong direction or parallel 
            }

            // compute d parameter using equation 2
            double d = N.dotProduct(v0);

            // compute t (equation 3)
            double t = (d - N.dotProduct(rayOrigin)) / NdotRayDirection;
            // check if the triangle is in behind the ray
            if (t < 0) {
                return -1; // the triangle is behind 
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
        // todo: Is not sensitive to one-sided rectangles
        //
        public static double rayTriangleIntersectNew(
                Vector3D rayOrigin, Vector3D rayDirection,
                Vector3D v0, Vector3D v1, Vector3D v2) {
            final double kEpsilon = 1E-12; // constant for "close enough to 0"
            double a, f, u, v;

            Vector3D edge1 = v1.subtract(v0);
            Vector3D edge2 = v2.subtract(v0);
            Vector3D h = rayDirection.crossProduct(edge2);
            a = edge1.dotProduct(h);
            if (a > -kEpsilon && a < kEpsilon) {
                return -1;    // This ray is parallel to this triangle.
            }

            f = 1.0 / a;
            Vector3D s = rayOrigin.subtract(v0);
            u = f * (s.dotProduct(h));
            if (u < 0.0 || u > 1.0) {
                return -1;
            }
            Vector3D q = s.crossProduct(edge1);
            v = f * rayDirection.dotProduct(q);
            if (v < 0.0 || u + v > 1.0) {
                return -1;
            }
            // At this stage we can compute t to find out where the intersection point is on the line.
            double t = f * edge2.dotProduct(q);
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
        final public static double eV = 1.60218e-19*1e4; // 1 eV in SI with cm
        // factor 1e4 is from using cm, not m here - 100^2
    }

    static public class Graphics {

        public static void drawSphere(Group g, Vector3D position, float radius, String webColor) {
            Sphere s = new Sphere(radius);
            s.setTranslateX(position.getX());
            s.setTranslateY(position.getY());
            s.setTranslateZ(position.getZ());
            final PhongMaterial pm = new PhongMaterial();
            pm.setSpecularColor(Color.web(webColor));
            pm.setDiffuseColor(Color.web(webColor));
            s.setMaterial(pm);

            Platform.runLater(() -> g.getChildren().add(s));
        }

        public static void drawLine(Group g, Vector3D p1, Vector3D p2, Color c) {
            Vector3D v = p2.subtract(p1);

            Cylinder line = new Cylinder(1, v.getNorm(), 4);
            PhongMaterial pm = new PhongMaterial(c);
            line.setMaterial(pm);

            double phi = java.lang.Math.atan2(v.getX(), v.getY()) * 180 / java.lang.Math.PI;
            double theta = java.lang.Math.acos(v.getZ() / v.getNorm()) * 180 / java.lang.Math.PI;

            line.getTransforms().add(new Translate((p2.getX() + p1.getX()) / 2,
                    (p2.getY() + p1.getY()) / 2, (p2.getZ() + p1.getZ()) / 2));
            line.getTransforms().add(new Rotate(180 - phi, new Point3D(0, 0, 1)));
            line.getTransforms().add(new Rotate(theta - 90, new Point3D(1, 0, 0)));

            Platform.runLater(() -> g.getChildren().add(line));
        }

        //
        // visualizeEvent
        //
        // will make a small golden sphere at the event point
        //
        public static void visualizeEvent(Event event, Group g) {
            if (event.code != Event.Code.Gone) {
                String color;
                switch (event.code) {
                    case Entry:
                        color = "green";
                        break;
                    case Exit:
                        color = "red";
                        break;
                    case Scatter:
                        color = "gold";
                        break;
                    case EmergencyExit:
                        color = "purple";
                        break;
                    case Capture:
                        color = "lightblue";
                        break;
                    default:
                        color = "black";
                        
                }
                Util.Graphics.drawSphere(g, event.position, 1, color);
                //System.out.println("Visualizing "+event.code+" event at " + event.position);
            }
        }

        public void displayEffect(Group g, Vector3D v, String color) {
            int scale = 2;

            Sphere s = new Sphere(scale / (double) 2);
            s.setMaterial(new PhongMaterial(Color.web(color, 0.2)));
            s.setDrawMode(DrawMode.FILL);
            s.setTranslateX(v.getX());
            s.setTranslateY(v.getY());
            s.setTranslateZ(v.getZ());
            s.setOpacity(0.2);

            // make it glow
            Glow glow = new Glow(1.0);
            glow.setLevel(1.0);
            s.setEffect(glow);

            // make it shrink over 2 seconds
            ScaleTransition t = new ScaleTransition(Duration.millis(500), s);
            t.setFromX(1);
            t.setFromY(1);
            t.setFromZ(1);

            t.setToX(0);
            t.setToY(0);
            t.setToZ(0);

            // kill it when done
            t.setOnFinished((e) -> g.getChildren().remove(s));

            // add to scene and play
            t.play();
            Platform.runLater(() -> g.getChildren().add(s));
        }

    }
}
