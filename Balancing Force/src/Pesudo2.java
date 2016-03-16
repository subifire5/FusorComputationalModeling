import java.util.*;
import java.io.*;

public class Pesudo2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Generating numbers");
		
		Random xran = new Random(); //positive points (Charge)
		Random yran = new Random();//Negative points (Charge)
		
		//String pos = "";
		//String[] neg = {};
		List<Integer> neg = new ArrayList<Integer>();
		List<Integer> pos = new ArrayList<Integer>();
		System.out.println("Postive values");
		for(int i = 0; i < 10; i++){ //Positve
			int pone = xran.nextInt(100); //x value
			int ptwo = xran.nextInt(100);// Y value    //These all equal one point for Positive
			int pthree = xran.nextInt(100); //Z value
			System.out.println("x: "+ pone + " | "+ "y: " + ptwo + " | " + "z: " + pthree);
	
			pos.add(pone); //Adding to pos Array
			pos.add(ptwo);
			pos.add(pthree);
			
		}

		System.out.println("Negative Points");
		for(int i = 0; i < 10; i++){ //Negatives
			int none = yran.nextInt(100); //x value
			int ntwo = yran.nextInt(100);// Y value    //These all equal one point for Positive
			int nthree = yran.nextInt(100); //Z value
			
			System.out.println("x: "+ none + " | "+ "y: " + ntwo + " | " + "z: " + nthree);
			,
			neg.add(none); //adding to Neg Array
			neg.add(ntwo);
			neg.add(nthree);
		}
		System.out.println("NEG Values");
		System.out.println(neg);
		System.out.println("POS Values");
		System.out.println(pos);
		
		/*MyPoint[] points = new MyPoint[10000];
		points[0] = new MyPoint (1, 2, 3);
		points[1] = new MyPoint();
		
		points[1].x = 0;
		if (points[1].Compare(points[2])){
			// these are equal
		}
		
		ArrayList<String> friends = new ArrayList<>();
		friends.add("Peter");
		friends.add("hewhohewgohiwe");
		friends.add("Peteer");
		friends.add("Petqwer");
		friends.remove(1);
		//System.out.println(friends);
		for(int i = 0; i< friends.size(); i++){
			System.out.println(friends.get(i));
		}	
		*/
	}

}
/*
class MyPoint
{
	public double x, y, z;
	
	MyPoint ()
	{
		this.x = 0;
		this.y = 0; 
		this.z = 0;
	}
	
	MyPoint (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public boolean Compare(MyPoint point2){
		return (this.x == point2.x && this.y == point2.y && this.z == point2.z);
	}

}
*/