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

    public Boolean checkTriangleCollision(Triangle inputTri, Vector A, Vector B) {
       
        // this is temporary, so I can test if the rest of the triangle class works
        return null;
        /*
        
       
        //Find plane given points P, Q, R
        Vector P = ;
        Vector Q = ; //testing
        Vector R = ;

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
        */
        
    }
    
}
