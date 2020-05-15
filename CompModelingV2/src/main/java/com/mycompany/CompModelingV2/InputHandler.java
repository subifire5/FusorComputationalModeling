/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.CompModelingV2;

/**
 *
 * @author subif
 */
import java.io.File;
import java.util.Scanner;

public class InputHandler {

    public InputHandler() {
    }

    public void getInput() {

        Scanner s = new Scanner(System.in);
        Boolean inputRecieved = false;
        String input = "";
        while (!inputRecieved) {
            System.out.println("Would you like to read from an Efield file (R) or generate one (G)");
            input = s.next();
            if (input.equals("R")) {
                inputRecieved = true;
            } else if (input.equals("G")) {
                inputRecieved = true;
            } else {
                System.out.println("Please respond with (R) or (G)");
            }
        }
        s.close();
        if (input.equals("R")) {
            readFromFile();
        } else {
            generateFile();
        }

    }

    public void readFromFile() {
        Scanner s = new Scanner(System.in);
        System.out.println("Please type in the negative charged file name");
        String negativeFileName = "";
        negativeFileName = s.nextLine();
        String positiveFileName = "";
        System.out.println("Please type in the positively charged file name");
        positiveFileName = s.nextLine();

        /*Geometry geometry = new Geometry(negativeFileName, 1.0, positiveFileName, -35000.0);
        geometry.initialize();        
        geometry.translateNegativeTriangles(new Vector(-30.0, 50.0, -80.0));
         */
        s.close();
    }

    public void generateFile() {
        String positiveFileName = "";
        String negativeFileName = "";
        String outputFileName = "";
        Scanner s = new Scanner(System.in);
        System.out.println("Please type cathode (negative) file name");
        negativeFileName = s.nextLine();
        // file Exists is just a way of testing if a given file exists.
        File fileExists = new File(negativeFileName);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            negativeFileName = s.nextLine();
            fileExists = new File(negativeFileName);
        }

        System.out.println("Please type in the positively charged file name");
        positiveFileName = s.nextLine();
        fileExists = new File(positiveFileName);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            positiveFileName = s.nextLine();
            fileExists = new File(positiveFileName);
        }

        System.out.println("Please enter your output file location");
        fileExists = new File(s.nextLine());
        while (fileExists.exists()) {
            System.out.println("This file already exists; do you want to replace it? Y/N");
            String input = "";
            input = s.nextLine();
            if (input.equals("Y")) {
                break;
            } else {
                System.out.println("Please enter your output file location");
                fileExists = new File(s.nextLine());
            }

        }
        
        System.out.println("How many charges do you want?");
        int numCharges = s.nextInt();
        
        System.out.println("What charge are the positive charges?");
        Double posCharge =  s.nextDouble();
        System.out.println("What charge are the negative charges?");
        Double negCharge = s.nextDouble();
        
        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        Double scaleDistance = s.nextDouble();
        
        
        System.out.println("How many shake-ups do you want?");
        int shakeUps = s.nextInt();
        
        
        
        
        s.close();

    }
}
