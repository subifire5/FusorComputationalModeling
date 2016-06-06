/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;


public final class RectHV {
    public double xmin, ymin, zmin;
    public double xmax, ymax, zmax;

    public RectHV(double xmin, double ymin, double xmax, double ymax, double zmin, double zmax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.zmin = zmin;
        this.zmax = zmax;
    }

    public boolean intersects(RectHV compare) {
        return this.xmax >= compare.xmin && this.ymax >= compare.ymin
            && compare.xmax >= this.xmin && compare.ymax >= this.ymin
            && this.zmax >= compare.zmin
            && compare.zmax >= this.zmin;
    }

    public boolean contains(Point p) {
        return (p.x >= xmin) && (p.x <= xmax)
            && (p.y >= ymin) && (p.y <= ymax)
            && (p.z >= zmin) && (p.z <= zmax);
    }
    
    public String toString() {
        return "[" + xmin + ", " + xmax + "] x [" + ymin + ", " + ymax + "] x [" + zmin + ", " + zmax + "]";
    }
}
