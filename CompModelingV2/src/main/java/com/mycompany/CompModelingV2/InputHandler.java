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

    int numCharges = 100;
    String positiveFilePath = "ThinRightPlate.csv";
    String negativeFilePath = "ThinPlate.csv";
    String outputFilePath = "outputFile.csv";
    String inputFilePath = "inputFilePath.csv";
    Double posCharge = 100.0;
    Double negCharge = 100.0;
    Double scaleDistance = 0.01;
    Double vAnnode;
    Double vCathode;
    int shakeUps = 100;
    Geometry geometry;
    ChargeDistributer chargeDistributer;
    Scanner s;
    EField eField;
    Charge[] charges;
    Charge[] positiveCharges;
    Charge[] negativeCharges;

    public InputHandler() {
    }

    public void getInput() {

        s = new Scanner(System.in);
        Boolean inputRecieved = false;
        String input = "";
        while (!inputRecieved) {
            System.out.println("Would you like to read from an Efield file (R) or generate one (G)");
            input = s.nextLine();
            if (input.equals("R")) {
                inputRecieved = true;
            } else if (input.equals("G")) {
                inputRecieved = true;
            } else {
                System.out.println("Please respond with (R) or (G)");
            }
        }
        if (input.equals("R")) {
            readFromFile();
        } else {
            generateFile();

        }
        s.close();

    }

    public void readFromFile() {
 

 

        System.out.println("Please enter your input file location");
        inputFilePath = s.nextLine();
        File fileExists = new File(inputFilePath);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            inputFilePath = s.nextLine();
            fileExists = new File(inputFilePath);
        }

        System.out.println("Please enter the annode (positive) voltage");
        vAnnode = s.nextDouble();
        
        System.out.println("Please enter the cathode (negative) voltage");
        vCathode = s.nextDouble();
        
        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        scaleDistance = s.nextDouble();
        
        EFieldFileParser parser = new EFieldFileParser();

        Charge[][] chargeArrayArray = parser.parseFile(inputFilePath);
        charges = chargeArrayArray[0];
        positiveCharges = chargeArrayArray[1];
        negativeCharges = chargeArrayArray[2];

        eField = new EField(charges, vAnnode, vCathode, scaleDistance);
    }

    public void generateFile() {
        positiveFilePath = "";
        negativeFilePath = "";
        outputFilePath = "";
        File fileExists;
        
        System.out.println("Please type in the annode (positive) file name");
        positiveFilePath = s.nextLine();
        fileExists = new File(positiveFilePath);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            positiveFilePath = s.nextLine();
            fileExists = new File(positiveFilePath);
        }
        System.out.println("Please type cathode (negative) file name");
        negativeFilePath = s.nextLine();
        // file Exists is just a way of testing if a given file exists.
        fileExists = new File(negativeFilePath);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            negativeFilePath = s.nextLine();
            fileExists = new File(negativeFilePath);
        }

        System.out.println("Please enter your output file location");
        outputFilePath = s.nextLine();
        fileExists = new File(outputFilePath);
        while (fileExists.exists()) {
            System.out.println("This file already exists; do you want to replace it? Y/N");
            String input = "";
            input = s.nextLine();
            if (input.equals("Y")) {
                break;
            } else {
                System.out.println("Please enter your output file location");
                fileExists = new File(outputFilePath);
            }

        }

        System.out.println("How many charges of each polarity do you want?");
        numCharges = s.nextInt();

        System.out.println("What charge are the positive charges?");
        posCharge = s.nextDouble();
        System.out.println("What charge are the negative charges?");
        negCharge = s.nextDouble();

        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        scaleDistance = s.nextDouble();

        System.out.println("How many shake-ups do you want?");
        shakeUps = s.nextInt();

        geometry = new Geometry(positiveFilePath, negCharge, negativeFilePath, posCharge);
        geometry.sumUpSurfaceArea();

        chargeDistributer = new ChargeDistributer(geometry, scaleDistance, numCharges);
        chargeDistributer.balanceCharges(shakeUps);
        charges = chargeDistributer.charges;
        EFieldFileWriter writer = new EFieldFileWriter(chargeDistributer);
        writer.writeCSV(outputFilePath);

    }
}
