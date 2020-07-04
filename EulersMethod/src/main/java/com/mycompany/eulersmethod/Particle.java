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

    public Vector pos;
    public Vector vel = new Vector(0.0, 0.0, 0.0);
    public int polarity;
    public Double time = 0.0;
    public Double mass = 2.014 * 1.66E-27;

    public Particle() {
    }

    public Particle(Double x, Double y, Double z) {

        this.pos = new Vector(x, y, z);
    }

    public Particle(Vector pos, Vector vel) {
        this(pos.x, pos.y, pos.z);
        this.vel = vel;
    }

    public Particle(Vector pos) {
        this(pos.x, pos.y, pos.z);
    }

    public Particle(Double x, Double y, Double z, int polarity) {
        this.pos = new Vector(x, y, z);
        this.polarity = polarity;
    }

    public Particle(Vector p, int polarity) {
        this(p.x, p.y, p.z, polarity);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param vx
     * @param vy
     * @param vz
     * @param polarity
     */
    public Particle(Double x, Double y, Double z, Double vx, Double vy, Double vz, int polarity) {
        this.pos = new Vector(x, y, z);
        this.vel = new Vector(vx, vy, vz);
        this.polarity = polarity;
    }

    /**
     *
     * @param p
     * @param v
     * @param polarity
     */
    public Particle(Vector p, Vector v, int polarity) {
        this(p.x, p.y, p.z, v.x, v.y, v.z, polarity);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param vx
     * @param vy
     * @param vz
     * @param polarity
     * @param time
     */
    public Particle(Double x, Double y, Double z, Double vx, Double vy, Double vz, int polarity, Double time) {
        this.pos = new Vector(x, y, z);
        this.vel = new Vector(vx, vy, vz);
        this.polarity = polarity;
        this.time = time;
    }

    /**
     *
     * @param p
     * @param v
     * @param polarity
     * @param time
     */
    public Particle(Vector p, Vector v, int polarity, Double time) {
        this(p.x, p.y, p.z, v.x, v.y, v.z, polarity, time);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param vx
     * @param vy
     * @param vz
     * @param polarity
     * @param time
     * @param mass
     */
    public Particle(Double x, Double y, Double z, Double vx, Double vy, Double vz, int polarity, Double time, Double mass) {
        this.pos = new Vector(x, y, z);
        this.vel = new Vector(vx, vy, vz);
        this.polarity = polarity;
        this.time = time;
        this.mass = mass;
    }

    /**
     *
     * @param p
     * @param v
     * @param polarity
     * @param time
     * @param mass
     */
    public Particle(Vector p, Vector v, int polarity, Double time, Double mass) {
        this(p.x, p.y, p.z, v.x, v.y, v.z, polarity, time, mass);
    }

    public Particle(String[] s) {
        this.pos = new Vector(Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]));
        this.vel = new Vector(Double.parseDouble(s[3]), Double.parseDouble(s[4]), Double.parseDouble(s[5]));
        this.polarity = Integer.parseInt(s[6]);
        this.time = Double.parseDouble(s[7]);
        this.mass = Double.parseDouble(s[8]);
    }

    @Override
    public String toString() {
        String particle = "Position Vector: " + pos.toString();
        particle += "Velocity Vector: " + vel.toString();
        particle += "Polarity: " + polarity;
        particle += "Time: " + time;
        particle += "Mass: " + mass;
        return particle;
    }

    public String[] toCSVString() {
        String[] csvString = {"" + this.pos.x, "" + this.pos.y, "" + this.pos.z,
            "" + this.vel.x, "" + this.vel.y, "" + this.vel.z,
            "" + this.polarity, "" + this.time, "" + this.mass};
        return csvString;
    }

}
