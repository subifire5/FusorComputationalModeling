import java.util.*;

public class Project1 {

	/**
	 * @param args
	 *            the command line arguments
	 */
	
	public static void main(String[] args) {
		System.out.println("Hello! What is 'your' number?");
		int[] numbers = { 3, 1, 4, 7, 9, 6, 4 };
		Scanner usrInput = new Scanner(System.in);
		int inputFinal = usrInput.nextInt();

		int min = numbers[0], max = numbers[0];
		
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
			}
				if (numbers[i] > max) {
					max = numbers[i];
				}
				if (inputFinal == numbers[i]) {
					System.out.println("The postion of " + inputFinal + " is: " + i); // prints
				}
				//System.out.println(i);
			}
		System.out.println("Minimum: " + min + " | Maximum: " + max);
		/*
		 * public static int search(int[] inputArray) { Scanner userInput = new
		 * Scanner(System.in); int finalInput = next.Line();
		 * 
		 * int n = 0; return n; }
		 */
	}
}
