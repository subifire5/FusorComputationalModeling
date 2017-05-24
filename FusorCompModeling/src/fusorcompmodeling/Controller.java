/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import static fusorcompmodeling.StatsGen.normalizedVector;
import java.util.List;

/**
 *
 * @author sfreisem-kirov
 */
public class Controller {

    public EField e = new EField();
    public RungeKutta s = new RungeKutta();
    public Atom[] atoms = new Atom[50];
    public int atomsInArray = 0;

    public Controller(Point[] points, double voltageAnnode, double voltageCathode) {
        e.setPoints(points);
        e.setkQ(voltageAnnode, voltageCathode);
        s.setEField(e);
    }

    public void stepAllForeward(Point[] points) {
        for (int i = 0; i < atomsInArray; i++) {
            Double ts = 0.0000000009;
            double vel = StatsGen.normalizedVector(atoms[i].Velocity);
            //double b10 = Math.log10(vel);
            System.out.println("Velocity: " + vel);
            //double ts = 0.000000000000001;
            Atom newAtom = s.moveForward(points, ts, atoms[i], e);
            atoms[i] = newAtom;
            System.out.println("Loop just ran once, atoms in array is " + atomsInArray);
        }
    }

    public double checkTimeStep(Atom a) {
        double v = normalizedVector(a.Velocity);
        System.out.println(v);
        double timeStep = 0;
        return timeStep;
    }

    public void addAtom(Point pos, double mass) {
        Vector v = new Vector();
        v.x = 0;
        v.y = 0;
        v.z = 0;
        Atom a = new Atom();
        a.position = pos;
        a.Velocity = v;
        a.mass = mass;
        atoms[atomsInArray] = a;
        atomsInArray = atomsInArray + 1;
    }
}
