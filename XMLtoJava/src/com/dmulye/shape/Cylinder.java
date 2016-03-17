package com.dmulye.shape;

import java.util.Arrays;

public class Cylinder {
	float radius;
	String shape;
	String charge;
	float[] point1;
	float[] point2;
	
	public Cylinder (float radius, String shape, float point1[], float point2[]) {
		this.radius = radius;
		this.charge = charge;
		this.shape = shape;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public String getCharge() { 
		return charge;
	}
	
	public String getShape() {
		return shape;
	}
	
	public float[] getPoint1() {
		return point1;
	}
	
	public float[] getPoint2() {
		return point2;
	}
	
	public String toString() {
        StringBuffer str = new StringBuffer( "Charge: " + getCharge() + "\n" +  "Radius: " + getRadius() + "\n" + "Point 1: " + Arrays.toString(getPoint1()) + "\n" +  "Point 2: " + Arrays.toString(getPoint2()) + "\n");
        str.append( "\n" );

        return str.toString();
	}
}
