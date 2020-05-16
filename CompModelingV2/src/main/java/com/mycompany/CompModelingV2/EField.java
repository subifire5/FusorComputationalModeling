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
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Transform;

public class EField {

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
     */
    public EField(Charge[] charges, double vAnnode, double vCathode, double scaleDistance) {
        this.vAnnode = vAnnode;
        this.vCathode = vCathode;
        this.charges = charges;
        this.k = 8.9875517923E9;
        this.scaleDistance = scaleDistance;

    }

    public EField(Xform positiveCharges, Xform negativeCharges, double vAnnode, double vCathode, double scaleDistance) {
        this.vAnnode = vAnnode;
        this.vCathode = vCathode;
        this.positiveCharges = positiveCharges;
        this.negativeCharges = negativeCharges;
        this.k = 8.9875517923E9;
        this.scaleDistance = scaleDistance;

    }

    // uses the positioning of each charge in the JavaFX Scene
    // instead of the saved positions that my be off
    public Vector effectOnCharge(Sphere s, int polarity) {
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
        return sumOfField;

    }

    public Vector effectOnCharge(Charge c) {
        Vector sumOfField = new Vector(0.0, 0.0, 0.0);
        double voltage;
        double v;
        double distanceSquared;

        for (Charge t : charges) {
            voltage = vAnnode - vCathode;
            v = c.polarity * t.polarity * voltage;
            distanceSquared = t.distanceSquared(c);
            Vector effectOnPoint;
            effectOnPoint = c.thisToThat(t).normalized();
            effectOnPoint.scale(-1.0);
            effectOnPoint.x *= v / distanceSquared;
            effectOnPoint.y *= v / distanceSquared;
            effectOnPoint.z *= v / distanceSquared;
            sumOfField.plusEquals(effectOnPoint);
        }

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
            ePotential += (t.polarity*k / (c.distanceTo(t))) * c.polarity;
        }
        return ePotential;
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
            ePotential += (t.polarity*k) / (v.distanceTo(t)*scaleDistance);
        }
        return ePotential;
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
                ePotential += (t.polarity*k) / (c.distanceTo(t)*scaleDistance);
            }
        }
        return ePotential;
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
                ePotential += (t.polarity*k) / (v.distanceTo(t)*scaleDistance);
            }
        }
        return ePotential;

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
                ePotential += (t.polarity*k / (c.distanceTo(t)*scaleDistance)) * c.polarity;
            }
        }
        return ePotential;
    }

}
