/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// PRAVEER AND MAGGIE, the package below is something you should definitely change to whatever you want your package name to be
// other notes
// you will most likely want the electric potential energy, not the electric potential
// as for the 'ignore charge', that's saying don't include the charge that we're finding the electric potential of
 package com.mycompany.EulersMethod;


/**
 *
 * @author subif
 */
import java.util.ArrayList;
import java.util.List;


public class EField {

    double chargeFactor = 1.0;
    double vAnnode;
    double vCathode;
    double scaleDistance;
    Charge[] charges;
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

    /**
     * This is the electric field at a given point assuming a positive charge of 1
     * @param v is the location (as a vector)
     * @return a force vector
     */
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
    /**
     * This is the electric field on a charge.
     * @param c is the charge
     * @return A force vector
     */
    public Vector effectOnCharge(Charge c) {
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
     * this is NOT the electric potential ENERGY
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
     * The electric potential ENERGY of a given charge without the inclusion of
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
