/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

import java.util.Random;

/**
 *
 * @author subif
 */
public class Triangle {
 
    public Vector[] points;
    public int polarity;
    public Double surfaceArea;
    
    Triangle(){}
    Triangle(Vector A, Vector B, Vector C, int polarity){
        this.points = new Vector[]{A, B, C};
        this.polarity = polarity;
        this.surfaceArea = getSurfaceArea();
    }
    Triangle(String[] triangle) {
        this.points[0] = new Vector(triangle[0], triangle[1], triangle[2]);
        this.points[1] = new Vector(triangle[3], triangle[4], triangle[5]);
        this.points[2] = new Vector(triangle[6], triangle[7], triangle[8]);
        this.polarity = Integer.valueOf(triangle[9]);
        this.surfaceArea = Double.valueOf(triangle[10]);
    }
    Triangle(Vector[] points, int polarity){
        this.points = points;
        this.polarity = polarity;
        this.surfaceArea = getSurfaceArea();
    }
    public int getPolarity(){
        return polarity;
    }
    
    public void translate(Vector offset){
        for(Vector point: points){
            point.plusEquals(offset);
        }
    }
    
    public void multiply(Vector factor){
        for(Vector point: points){
            point.multiply(factor);
            point.product(factor);
        }
    }
    
    public Double getSurfaceArea(){
        // according to stack exchange
        // https://math.stackexchange.com/questions/128991/how-to-calculate-the-area-of-a-3d-triangle
        // three points in 3d A, B, and C
        // first find angle theta between AB and AC
        // then find area of triangle using
        // Area= 1/2|AB||AC|sin(theta)
       
        Vector AB = points[0].thisToThat(points[1]);

        if(AB.norm().isNaN()){
            System.out.println("problem AB");
        }
        Vector AC = points[0].thisToThat(points[2]);
        if(AC.norm().isNaN()){
            System.out.println("Problem AC");
        }
        if(points[0].equals(points[1])){
            System.out.println("hoijo");
        }
        if(points[1].equals(points[2])){
            System.out.println("ueoi");
        }
        Double theta = AB.angleBetween(AC);
        if(theta.isNaN()){
            System.out.println("problem theta");
        }
        surfaceArea = 0.5*AB.norm()*AC.norm()*(Math.sin(theta));
        return surfaceArea;
    }



    /**
     * Generates a random vector on this triangle
     * Then creates a charge at that point with the polarity of the triangle
     * @return A random charge on this triangle 
     */
    public Charge genRandCharge(){
        //generate a random point p uniformly from within triangle ABC
        //https://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
        Charge charge = new Charge();
        Random randGen = new Random();
        double r1 = randGen.nextDouble(); // generates a double between 0.0 and 1.0
        double r2 = randGen.nextDouble();
        double sqr1 = Math.sqrt(r1);
        charge.pos.x = (points[0].x*(1-sqr1))+(points[1].x*(sqr1*(1-r2)))+(points[2].x*r2*sqr1);
        charge.pos.y = (points[0].y*(1-sqr1))+(points[1].y*(sqr1*(1-r2)))+(points[2].y*r2*sqr1);
        charge.pos.z = (points[0].z*(1-sqr1))+(points[1].z*(sqr1*(1-r2)))+(points[2].z*r2*sqr1);
        charge.polarity = polarity;

        return charge;
    }   
    
    public Charge testGenRandCharge(){
        // doesn't work right now
        // if tested with the two files "ThinPlate.stl" and "ThinRightPlate.stl"
        // creates a really neat x pattern instead of the squares
        //https://jsfiddle.net/jniac/fmx8bz9y/
        Charge charge = new Charge();
        Random randGen = new Random();
        double r1 = randGen.nextDouble();
        double r2 = randGen.nextDouble();
        if(r1+r2>1){
            r1 = 1-r1;
            r2 = 1-r2;
        }
        
        charge.pos.x = points[0].x + points[0].thisToThat(points[1]).x*r1 + points[1].thisToThat(points[2]).x*r2;
        charge.pos.y = points[0].y + points[0].thisToThat(points[1]).y*r1 + points[1].thisToThat(points[2]).y*r2;
        charge.pos.z = points[0].z + points[0].thisToThat(points[1]).z*r1 + points[1].thisToThat(points[2]).z*r2;
        charge.polarity = polarity;
        return charge;
    }

    
    @Override
    public String toString(){
        String triangle = "";
        triangle += "Point A: " +points[0] + " \n";
        triangle += "Point B: " +points[1] + " \n";
        triangle += "Point C: " +points[2] + " \n";
        return triangle;
    }
    
    /**
     * Returns a string version of this Triangle suitable for printing to a CSV
     * file
     *
     * @return CSV string
     */
    public String[] toCSVString() {
        String[] csvString = {"" + this.points[0].x, "" + this.points[0].y, "" + this.points[0].z,
            "" + this.points[1].x, "" + this.points[1].y, "" + this.points[1].z,
            "" + this.points[2].x, "" + this.points[2].y, "" + this.points[2].z, "" + this.polarity,
            "" + this.surfaceArea};
        return csvString;
    }
    
    public static double checkTriangleCollision(Triangle inputTri, Vector A, Vector B) {
        // Return -1 if ray does not intersect triangle (example: ray is parallel to triangle)
        
        // Split two vector components of ray (origin and direction) into x, y, z
        double ox = A.x; // A: Origin/Starting point
        double oy = A.y;
        double oz = A.z;
        double dx = B.x; // B: Vector's 'slope'
        double dy = B.y;
        double dz = B.z;
        
        // Split three points which make up/define plane into x, y, z
        Vector v0 = inputTri.points[0]; // Read point 1 (P) from triangle
        double v0x = v0.x;
        double v0y = v0.y;
        double v0z = v0.z;             
        Vector v1 = inputTri.points[1]; // Read point 2 (Q) from triangle
        double v1x = v1.x;
        double v1y = v1.y;
        double v1z = v1.z;      
        Vector v2 = inputTri.points[2]; // Read point 3 (R) from triangle
        double v2x = v2.x;
        double v2y = v2.y;
        double v2z = v2.z;

        final double kEpsilon = 1E-12; // constant for "close enough to 0"
        double a, f, u, v;

        double edge1x = v1x - v0x;
        double edge1y = v1y - v0y;
        double edge1z = v1z - v0z;

        double edge2x = v2x - v0x;
        double edge2y = v2y - v0y;
        double edge2z = v2z - v0z;

        double hx = dy * edge2z - dz * edge2y;
        double hy = dz * edge2x - dx * edge2z;
        double hz = dx * edge2y - dy * edge2x;

        a = edge1x * hx + edge1y * hy + edge1z * hz;

        if (a > -kEpsilon && a < kEpsilon) {
            return -1;    // This ray is parallel to this triangle.
        }

        double nx = edge1y * edge2z - edge1z * edge2y;
        double ny = edge1z * edge2x - edge1x * edge2z;
        double nz = edge1x * edge2y - edge1y * edge2x;

        double nDotDir = nx * dx + ny * dy + nz * dz;
        if (nDotDir > -kEpsilon) {
            return -1; // wrong direction or parallel 
        }

        f = 1.0 / a;
        double sx = ox - v0x;
        double sy = oy - v0y;
        double sz = oz - v0z;

        u = f * (sx * hx + sy * hy + sz * hz);
        if (u < 0.0 || u > 1.0) {
            return -1;
        }

        double qx = sy * edge1z - sz * edge1y;
        double qy = sz * edge1x - sx * edge1z;
        double qz = sx * edge1y - sy * edge1x;

        v = f * (dx * qx + dy * qy + dz * qz);
        if (v < 0.0 || u + v > 1.0) {
            return -1;
        }

        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * (edge2x * qx + edge2y * qy + edge2z * qz);
//            if (t > kEpsilon) {
        if (t >= 0) {
            return t;
        } else {
            return -1;
        }
    }
/*
    public Boolean checkTriangleCollision(Triangle inputTri, Vector A, Vector B) {
       
        //Find plane given points P, Q, R
        Vector P = inputTri.points[0];
        Vector Q = inputTri.points[1];
        Vector R = inputTri.points[2];
        
        //Create two vectors from triangle sides
        Vector v1 = P.thisToThat(Q);
        Vector v2 = P.thisToThat(R);

        //Calculate normal to plane (cross product of triangle sides)
        Vector coeff = v1.crossProduct(v2);

        //Plug plane equation into parametric line equation
        Vector slope = A.thisToThat(B);
        double t = (coeff.x * (P.x - A.x) + coeff.y * (P.y - A.y) + coeff.z * (P.z - A.z)) / (coeff.x * slope.x + coeff.y * slope.y + coeff.z * slope.z);

        //Plug stepsize (t) into line equation to get intersection with plane
        Vector L = A.plusEquals(slope.scale(t));

        //Check if intersection of line and plane is in triangle PQR
        //Create two normals to the plane (and to the triangle)
        Vector c1 = (P.thisToThat(Q).crossProduct(P.thisToThat(L)));
        Vector c2 = (P.thisToThat(Q).crossProduct(R.thisToThat(L)));

        //Check if the two cross products face in the same direction using a dot product
        return (c1.dotProduct(c2) >= 0);       
    
    }
*/
    
}
