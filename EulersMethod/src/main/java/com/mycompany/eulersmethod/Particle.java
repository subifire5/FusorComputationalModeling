/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/**
 *
 * @author subif
 */
public class Particle extends Charge {

    public int polarity;
    public Double x;
    public Double y;
    public Double z;
    public Double EP;
    public Double mass = 2.04 * 1.66E-27;
    public Vector vel = new Vector(0.0, 0.0, 0.0);

    public Particle() {
    }

    public Particle(Double x, Double y, Double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Particle(Vector pos, Vector vel) {
        this(pos.x, pos.y, pos.z);
        this.vel = vel;
    }

    public Particle(Vector pos) {
        this(pos.x, pos.y, pos.z);
    }

    public Particle(Double x, Double y, Double z, int polarity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.polarity = polarity;
    }

    public Particle(Vector v, int polarity) {
        this(v.x, v.y, v.z, polarity);
    }

    public Particle(Double x, Double y, Double z, int polarity, Double EP) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.polarity = polarity;
        this.EP = EP;
    }

    public Particle(Vector v, int polarity, double EP) {
        this(v.x, v.y, v.z, polarity, EP);
    }

    public Particle(String[] s) {
        this.x = Double.parseDouble(s[0]);
        this.y = Double.parseDouble(s[1]);
        this.z = Double.parseDouble(s[2]);
        this.polarity = Integer.parseInt(s[3]);
    }

    @Override
    public String toString() {
        String particle = "Position Vector: ";
        particle += new Vector(x, y, z).toString();
        particle += "Velocity Vector: " + vel.toString();
        particle += "Polarity: " + polarity;
        return particle;
    }

    public String[] toCSVString() {
        String[] csvString = {"" + this.x, "" + this.y, "" + this.z, "" + this.polarity};
        return csvString;
    }

    public void scale(Double s) {
        x *= s;
        y *= s;
        z *= s;
    }

    /**
     * Adds a scalar to this charge's position vector (the += sign)
     *
     * @param s the scalar
     */
    public void plusEquals(Double s) {
        this.x += s;
        this.y += s;
        this.z += s;
    }

    /**
     * Sets the position of this charge to a vector
     *
     * @param v the vector
     */
    public void setPosition(Vector v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Sets the position of this charge to a different charge's position Use
     * instead of this = charge;
     *
     * @param c the other charge
     */
    public void setPosition(Charge c) {
        this.x = c.x;
        this.y = c.y;
        this.z = c.z;
    }
}
