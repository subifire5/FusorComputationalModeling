package com.dmulye.shape;

import java.util.Arrays;

public class Torus {
	float radius;
	String shape;
	String charge;
	float[] point1;
	float[] point2;
	float[] point3;
	float[] gap1;
	float[] gap2;
	
	public Torus (
			float radius,
			String shape, 
			float point1[], 
			float point2[], 
			float point3[], 
			String charge, 
			float gap1[], 
			float gap2[]) {
		this.radius = radius;
		this.charge = charge;
		this.shape = shape;
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		this.gap1 = gap1;
		this.gap2 = gap2;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public String getShape() {
		return shape;
	}
	
	public String getCharge() {
		return charge;
	}
	
	public float[] getPoint1() {
		return point1;
	}
	
	public float[] getPoint2() {
		return point2;
	}
	
	public float[] getPoint3() {
		return point3;
	}
	
	public float[] getGap1() {
		return gap1;
	}
	
	public float[] getGap2() {
		return gap2;
	}
	
	
	public String toString() {
        StringBuffer str = new StringBuffer( "Charge: " + getCharge() + "\n" +  "Radius: " + getRadius() + "\n" + "Point 1: " + Arrays.toString(getPoint1()) + "\n" +  "Point 2: " + Arrays.toString(getPoint2()) + "\n" +  "Point 3: " + Arrays.toString(getPoint3()) + "\n Gap Point 1: " + Arrays.toString(getGap1()) + "\n Gap Point 2: " + Arrays.toString(getGap2()));
        str.append( "\n" );

        return str.toString();
	}
}

