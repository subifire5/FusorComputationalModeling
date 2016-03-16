import java.util.*;
import java.io.*;
//import allPoints;
public class Psudo3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Testing Rand functions
		/*Random randomGenerator = new Random();
		Random random1 = new Random();
		int q = random1.nextInt(10);
		int w = random1.nextInt(10);
		int e = random1.nextInt(10);
		System.out.println(q);
		System.out.println(w);
		System.out.println(e);
		*/
		Random randomGenerator = new Random(); //This will be replaced by jakes stuff
		Random random1 = new Random();
		Random random2 = new Random();
		
		int[] posPoints = new int[10];
		int[] negPoints = new int[10];
		for (int i = 0; i < posPoints.length; i++){ // VERY IMPORTNAT that pos and negative arryas have to be same number
			
			int posran = random1.nextInt(100);
			int negran = random2.nextInt(100);
			posPoints[i] = posran;  //This will be Jakes function
			negPoints[i] = negran;
			
		}
		int posArrayLocation = random1.nextInt(posPoints.length);
		int negArrayLocation = random2.nextInt(negPoints.length);
		
		int ranPOS = posPoints[posArrayLocation];
		int ranNEG = negPoints[posArrayLocation]; // Finding random postions in array
		
		//int e = random1.nextInt(10)
		//allPoints points = new allPoints(posPoints[], negPoints[]);
		
		
		
		System.out.println("Pos Vals: " + Arrays.toString(posPoints));
		System.out.println("Neg vals: " + Arrays.toString(negPoints));
		
		System.out.println("pos array, neg array: " + posArrayLocation + " " + negArrayLocation);
		System.out.println("Pos, neg: " + ranPOS + ", " + ranNEG);
	}

}
