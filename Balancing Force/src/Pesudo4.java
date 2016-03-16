import java.util.*;

public class Pesudo4 {

      public static void main(String[] args) {
            // TODO Auto-generated method stub
            System.out.println("Initialized");
            double[] x = new double[100];
            double[] y = new double[100];
            double[] z = new double[100];
            int q = 10;
            int w = q[50%];
            System.out.println();
       
            int[] a = { 0, 5, 0 };
            int[] b = { 5, 0, 0 };
            // int[] c = {0,0,0}; //No Need for C
            Random ran1 = new Random();
            double ranPoint = ran1.nextDouble(); // next double cannot contain range
                                                                        // directly.
            ranPoint = ranPoint % 6.28318530718; // setting range

            double r = 1.3001;
            int track = 0;
            
            for (int j = 0; j < 99; j++) {
                  for (double i = 0; i < 6.28318530718; i = i + ranPoint) { // 6.28 is Two Pi
                        double x2 = r * Math.sin(i) * b[0];
                        x[j] = x2;
                        double y2 = (r * Math.cos(i) * a[1]); // Cut out C
                        y[j] = y2; // Cut out alot of functions
                        double z2 = 0;
                        z[j] = z2;
                  }
            }
            System.out.println("X values:" + Arrays.toString(x));
            System.out.println("Y values:" + Arrays.toString(y));
            System.out.println("Z values:" + Arrays.toString(z));
           
      }

}
