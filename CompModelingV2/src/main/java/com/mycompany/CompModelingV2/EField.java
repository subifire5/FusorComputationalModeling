/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.CompModelingV2;
// temp edits made to field effect at point
//TEMPORARY FOR NOW

/**
 *
 * @author subif
 */
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Transform;

public class EField {

    double chargeFactor = 1.0;
    double vAnnode;
    double vCathode;
    double scaleDistance;
    Charge[] charges;
    Xform positiveCharges;
    Xform negativeCharges;
    final Double k;  // Coulombs Constant

    public EField() {
        this.k = 8.9875517923E9;

    }

    /**
     *
     * @param charges a list of charges generating the electric field
     * @param vAnnode the + side voltage
     * @param vCathode the - side voltage
     * @param scaleDistance the distance in meters of one unit
     * @param centerOfGrid a vector pointing to the center of the wire grid,
     * defaults to origin
     */
    public EField(Charge[] charges, double vAnnode, double vCathode, double scaleDistance, Vector centerOfGrid) {
        this.vAnnode = vAnnode;
        this.vCathode = vCathode;
        this.charges = charges;
        this.k = 8.9875517923E9;
        this.scaleDistance = scaleDistance;
        if (centerOfGrid == null) {
            centerOfGrid = new Vector(0.0, 0.0, 0.0);
        }
        chargeFactor = (vAnnode - vCathode) / electricPotential(centerOfGrid);

    }

    public EField(Xform positiveCharges, Xform negativeCharges, double vAnnode, double vCathode, double scaleDistance, Vector centerOfGrid) {
        this.vAnnode = vAnnode;
        this.vCathode = vCathode;
        this.positiveCharges = positiveCharges;
        this.negativeCharges = negativeCharges;
        this.k = 8.9875517923E9;
        this.scaleDistance = scaleDistance;
        if (centerOfGrid == null) {
            centerOfGrid = new Vector(0.0, 0.0, 0.0);
        }
        chargeFactor = (vAnnode - vCathode) / electricPotential(centerOfGrid);
    }

    /**
     *
     * @param axis
     * @param lowerBound
     * @param upperBound
     * @param numberOfPotentials
     * @return
     */
    public Double[][] potentialTable(String axis, double lowerBound, double upperBound, int numberOfPotentials) {
        Double[][] potentials = new Double[numberOfPotentials][2];
        Double gap = (upperBound - lowerBound) / numberOfPotentials;
        if (axis.equals("X") || axis.equals("x")) {
            Double x = lowerBound;
            for (int i = 0; i < numberOfPotentials; i++) {
                Vector point = new Vector(x, 0.0, 0.0);
                potentials[i][0] = x;
                potentials[i][1] = electricPotential(point);
                x += gap;

            }
        } else if (axis.equals("Y") || axis.equals("y")) {
            Double y = lowerBound;
            for (int i = 0; i < numberOfPotentials; i++) {
                Vector point = new Vector(0.0, y, 0.0);
                potentials[i][0] = y;
                potentials[i][1] = electricPotential(point);
                y += gap;

            }

        } else {
            Double z = lowerBound;
            for (int i = 0; i < numberOfPotentials; i++) {
                Vector point = new Vector(0.0, 0.0, z);
                potentials[i][0] = z;
                potentials[i][1] = electricPotential(point);
                z += gap;

            }
        }
        return potentials;
    }

    /**
     *
     * @param bottomLeftCorner
     * @param upperRightCorner
     * @param numberOfPotentials
     * @return
     */
    public Double[][] potentialSlice(String graphPlane, Vector bottomLeftCorner, Vector upperRightCorner, int numberOfPotentials) {
        if (graphPlane.equals("XZ") || graphPlane.equals("xz") || graphPlane.equals("xZ") || graphPlane.equals("Xz")) {
            Double[][] potentials = new Double[numberOfPotentials * numberOfPotentials][3];
            Double xGap = (upperRightCorner.x - bottomLeftCorner.x) / numberOfPotentials;
            Double zGap = (upperRightCorner.z - bottomLeftCorner.z) / numberOfPotentials;
            Double x = bottomLeftCorner.x;
            Double z = bottomLeftCorner.z;

            for (int i = 0; i < numberOfPotentials; i++) {
                x = bottomLeftCorner.x;
                for (int j = 0; j < numberOfPotentials; j++) {
                    Vector point = new Vector(x, bottomLeftCorner.y, z);
                    potentials[(i * numberOfPotentials) + j][0] = x;
                    potentials[(i * numberOfPotentials) + j][1] = z;
                    potentials[(i * numberOfPotentials) + j][2] = electricPotential(point);
                    x += xGap;
                }
                System.out.println(i + "/" + numberOfPotentials + " completed");
                z += zGap;
            }
            System.out.println("returning potentials");
            return potentials;
        } else {

            Double[][] potentials = new Double[numberOfPotentials * numberOfPotentials][3];
            Double xGap = (upperRightCorner.x - bottomLeftCorner.x) / numberOfPotentials;
            Double yGap = (upperRightCorner.y - bottomLeftCorner.y) / numberOfPotentials;
            Double x = bottomLeftCorner.x;
            Double y = bottomLeftCorner.y;
            for (int i = 0; i < numberOfPotentials; i++) {
                x = bottomLeftCorner.x;
                for (int j = 0; j < numberOfPotentials; j++) {
                    Vector point = new Vector(x, y, bottomLeftCorner.z);
                    potentials[(i * numberOfPotentials) + j][0] = x;
                    potentials[(i * numberOfPotentials) + j][1] = y;
                    potentials[(i * numberOfPotentials) + j][2] = electricPotential(point);
                    x += xGap;
                }
                System.out.println(i + "/" + numberOfPotentials + " completed");

                y += yGap;
            }
            return potentials;
        }
    }

    public Vector[][] forceVectorLine(String axis, double lowerBound, double upperBound, int numberOfVectors) {
        Vector[][] fVectors = new Vector[numberOfVectors][2];
        Double gap = (upperBound - lowerBound) / numberOfVectors;
        if (axis.equals("X") || axis.equals("x")) {
            Double x = lowerBound;
            for (int i = 0; i < numberOfVectors; i++) {
                Vector point = new Vector(x, 0.0, 0.0);
                fVectors[i][0] = point;
                fVectors[i][1] = fieldAtPoint(point);
                x += gap;

            }
        } else if (axis.equals("Y") || axis.equals("y")) {
            Double y = lowerBound;
            for (int i = 0; i < numberOfVectors; i++) {
                Vector point = new Vector(0.0, y, 0.0);
                fVectors[i][0] = point;
                fVectors[i][1] = fieldAtPoint(point);
                y += gap;

            }

        } else {
            Double z = lowerBound;
            for (int i = 0; i < numberOfVectors; i++) {
                Vector point = new Vector(0.0, 0.0, z);
                fVectors[i][0] = point;
                fVectors[i][1] = fieldAtPoint(point);
                z += gap;

            }
        }
        return fVectors;
    }

    public Vector[][] forceVectorSlice(String graphPlane, Vector bottomLeftCorner, Vector upperRightCorner, int numberOfVectors) {
        if (graphPlane.equals("XZ") || graphPlane.equals("xz") || graphPlane.equals("xZ") || graphPlane.equals("Xz")) {
            Vector[][] fVectors = new Vector[numberOfVectors * numberOfVectors][2];
            Double xGap = (upperRightCorner.x - bottomLeftCorner.x) / numberOfVectors;
            Double zGap = (upperRightCorner.z - bottomLeftCorner.z) / numberOfVectors;
            Double x = bottomLeftCorner.x;
            Double z = bottomLeftCorner.z;

            for (int i = 0; i < numberOfVectors; i++) {
                x = bottomLeftCorner.x;
                for (int j = 0; j < numberOfVectors; j++) {
                    Vector point = new Vector(x, bottomLeftCorner.y, z);
                    fVectors[(i * numberOfVectors) + j][0] = point;
                    fVectors[(i * numberOfVectors) + j][1] = fieldAtPoint(point);
                    x += xGap;
                }
                System.out.println(i + "/" + numberOfVectors + " completed");
                z += zGap;
            }
            System.out.println("returning force vectors");
            return fVectors;
        } else {

            Vector[][] fVectors = new Vector[numberOfVectors * numberOfVectors][2];
            Double xGap = (upperRightCorner.x - bottomLeftCorner.x) / numberOfVectors;
            Double yGap = (upperRightCorner.y - bottomLeftCorner.y) / numberOfVectors;
            Double x = bottomLeftCorner.x;
            Double y = bottomLeftCorner.y;

            for (int i = 0; i < numberOfVectors; i++) {
                x = bottomLeftCorner.x;
                for (int j = 0; j < numberOfVectors; j++) {
                    Vector point = new Vector(x, y, bottomLeftCorner.z);
                    fVectors[(i * numberOfVectors) + j][0] = point;
                    fVectors[(i * numberOfVectors) + j][1] = fieldAtPoint(point);
                    x += xGap;
                }
                System.out.println(i + "/" + numberOfVectors + " completed");
                y += yGap;
            }
            System.out.println("returning force vectors");
            return fVectors;
        }
    }

    // uses the positioning of each charge in the JavaFX Scene
    // instead of the saved positions that my be off
    public Vector forceOnCharge(Sphere s, int polarity) {
        Vector sumOfField = new Vector(0.0, 0.0, 0.0);
        List<Node> posNodes = positiveCharges.getChildren();
        List<Node> negNodes = negativeCharges.getChildren();
        Transform sTransform = s.getLocalToSceneTransform();
        Charge c = new Charge(sTransform.getTx(), sTransform.getTy(), sTransform.getTz(), polarity);
        for (int i = 0; i < posNodes.size(); i++) {
            Transform tTransform = posNodes.get(i).getLocalToSceneTransform();
            Charge t = new Charge(tTransform.getTx(), tTransform.getTy(), tTransform.getTz(), 1);
            double voltage;
            voltage = vAnnode - vCathode;
            double v = c.polarity * t.polarity * voltage;
            double distanceSquared = t.distanceSquared(c);
            Vector effectOnPoint;
            effectOnPoint = c.thisToThat(t).normalized();
            effectOnPoint.scale(-1.0);
            effectOnPoint.x *= v / distanceSquared;
            effectOnPoint.y *= v / distanceSquared;
            effectOnPoint.z *= v / distanceSquared;
            sumOfField.plusEquals(effectOnPoint);
        }
        for (int i = 0; i < negNodes.size(); i++) {
            Transform tTransform = negNodes.get(i).getLocalToSceneTransform();
            Charge t = new Charge(tTransform.getTx(), tTransform.getTy(), tTransform.getTz(), -1);
            double voltage;
            voltage = vAnnode - vCathode;
            double v = c.polarity * t.polarity * voltage;
            double distanceSquared = t.distanceSquared(c);
            Vector effectOnPoint;
            effectOnPoint = c.thisToThat(t).normalized();
            effectOnPoint.scale(-1.0);
            effectOnPoint.x *= v / distanceSquared;
            effectOnPoint.y *= v / distanceSquared;
            effectOnPoint.z *= v / distanceSquared;
            sumOfField.plusEquals(effectOnPoint);
        }
        sumOfField.scale(chargeFactor);
        return sumOfField;

    }

    public Vector fieldAtPoint(Vector v) {
        Vector sumOfField = new Vector(0.0, 0.0, 0.0);
        double voltage;
        double vol;
        double distanceSquared;

        for (Charge t : charges) {
            voltage = vAnnode - vCathode;
            if (t.polarity > 0) {
                vol = -voltage;
            } else {
                vol = t.polarity * voltage;
            }
            distanceSquared = t.distanceSquared(v);
            Vector effectOnPoint;
            effectOnPoint = v.thisToThat(t).normalized();
            //effectOnPoint.scale(-1.0);
            effectOnPoint.x *= vol / distanceSquared;
            effectOnPoint.y *= vol / distanceSquared;
            effectOnPoint.z *= vol / distanceSquared;
            sumOfField.plusEquals(effectOnPoint);
        }
        sumOfField.scale(chargeFactor);
        return sumOfField;
    }

    public Vector forceOnCharge(Charge c) {
        Vector sumOfField = new Vector(0.0, 0.0, 0.0);
        double voltage;
        double vol;
        double distanceSquared;

        for (Charge t : charges) {
            voltage = vAnnode - vCathode;
            if (t.polarity > 0) {
                vol = -c.polarity * voltage;
            } else {
                vol = c.polarity * t.polarity * voltage;
            }
            //vol = c.polarity * t.polarity * voltage;
            distanceSquared = t.distanceSquared(c);
            Vector effectOnPoint;
            effectOnPoint = c.thisToThat(t).normalized();
            //effectOnPoint.scale(-1.0);
            effectOnPoint.x *= vol / distanceSquared;
            effectOnPoint.y *= vol / distanceSquared;
            effectOnPoint.z *= vol / distanceSquared;
            sumOfField.plusEquals(effectOnPoint);
        }
        sumOfField.scale(chargeFactor);
        return sumOfField;
    }

    /**
     * The electric potential of a specific charge
     *
     * @param c Selected charge
     * @return electric potential of a given charge
     */
    public double electricPotential(Charge c) {
        double ePotential = 0;
        for (Charge t : charges) {
            ePotential += (t.polarity * k / (c.distanceTo(t) * scaleDistance)) * c.polarity;
        }
        return ePotential * chargeFactor;
    }

    /**
     * The electric potential of a specific point
     *
     * @param v Selected point
     * @return electric potential of a given point
     */
    public double electricPotential(Vector v) {
        double ePotential = 0;
        for (Charge t : charges) {
            ePotential += (t.polarity * k) / (v.distanceTo(t) * scaleDistance);
        }
        return ePotential * chargeFactor;
    }

    /**
     * The electric potential of a given charge without the inclusion of a
     * specified charge
     *
     * @param c Selected Charge
     * @param ignore charge to be ignored in calculations
     * @return electric potential of a given charge
     */
    public double electricPotential(Charge c, Charge ignore) {
        double ePotential = 0;
        for (Charge t : charges) {
            if (t != ignore) {
                ePotential += (t.polarity * k) / (c.distanceTo(t) * scaleDistance);
            }
        }
        return ePotential * chargeFactor;
    }

    /**
     * The electric potential of a given point without the inclusion of a
     * specified charge
     *
     * @param v Selected point
     * @param ignore charge to be ignored in calculations
     * @return electric potential of a given point
     */
    public double electricPotential(Vector v, Charge ignore) {
        double ePotential = 0;
        for (Charge t : charges) {
            if (t != ignore) {
                ePotential += (t.polarity * k) / (v.distanceTo(t) * scaleDistance);
            }
        }
        return ePotential * chargeFactor;

    }

    /**
     * The electric potential energy of a given charge without the inclusion of
     * a specified charge
     *
     * @param c Selected Charge
     * @param ignore charge to be ignored in calculations
     * @return electric potential of a given charge
     */
    public double electricPotentialEnergy(Charge c, Charge ignore) {
        double ePotential = 0;
        for (Charge t : charges) {
            if (t != ignore) {
                ePotential += (t.polarity * k / (c.distanceTo(t) * scaleDistance)) * c.polarity;
            }
        }
        return ePotential * chargeFactor;
    }

}
