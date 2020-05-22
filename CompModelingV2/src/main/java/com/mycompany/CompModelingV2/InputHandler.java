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
            if (input.equals("R") || input.equals("r")) {
                inputRecieved = true;
            } else if (input.equals("G") || input.equals("g")) {
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

    // gets user input on selecting a file for input
    String fileNameGet(String mainText) {
        System.out.println(mainText);
        String filePath = s.nextLine();
        File fileExists = new File(filePath);
        System.out.println(fileExists);
        while (!fileExists.exists()) {
            System.out.println("this file does not exist; please enter a valid file name");
            filePath = s.nextLine();
            System.out.println(filePath);
            fileExists = new File(filePath);
        }
        return filePath;
    }

    // gets user input for creating a file
    String fileCreate(String mainText) {
        System.out.println(mainText);
        String filePath = s.nextLine();
        File fileExists = new File(filePath);
        while (fileExists.exists()) {
            System.out.println("This file already exists; do you want to replace it? Y/N");
            String input = "";
            input = s.nextLine();
            if (input.equals("Y") || input.equals("y")) {
                break;
            } else {
                System.out.println(mainText);
                fileExists = new File(filePath);
            }

        }
        return filePath;
    }

    public void readFromFile() {
        boolean inputRecieved = false;
        boolean makeGraph = false;
        Integer gaps = 0;
        Double lowerBound = 0.0;
        Double upperBound = 0.0;
        Double[][] potentialsTable;
        String tableFilePath = "";
        inputFilePath = fileNameGet("Please enter your input file location");

        System.out.println("Please enter the annode (positive) voltage");
        vAnnode = Double.valueOf(s.nextLine());

        System.out.println("Please enter the cathode (negative) voltage");
        vCathode = Double.valueOf(s.nextLine());

        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        scaleDistance = Double.valueOf(s.nextLine());

        String input = "";
        while (!inputRecieved) {
            System.out.println("Do you want to create a table of potentials? (Y/N)");

            input = s.nextLine();
            if (input.equals("Y") || input.equals("y")) {
                inputRecieved = true;
                makeGraph = true;
            } else if (input.equals("N") || input.equals("n")) {
                inputRecieved = true;
            } else {
                System.out.println("Please respond with (Y) or (N)");
            }
        }
        String graphAxis = "";
        if (makeGraph) {
            inputRecieved = false;
            while (!inputRecieved) {
                System.out.println("Along which axis do you want your graph? (X/Y/Z)");

                graphAxis = s.nextLine();
                if (graphAxis.equals("Y") || graphAxis.equals("y")) {
                    inputRecieved = true;
                } else if (graphAxis.equals("X") || graphAxis.equals("x")) {
                    inputRecieved = true;
                } else if (graphAxis.equals("Z") || graphAxis.equals("z")) {
                    inputRecieved = true;
                } else {
                    System.out.println("Please respond with (X), (Y) or (Z)");
                }

            }
            inputRecieved = false;
            System.out.println("How many points do you want plotted?");
            gaps = Integer.valueOf(s.nextLine());

            System.out.println("What is the lower bound");
            lowerBound = Double.valueOf(s.nextLine());

            System.out.println("What is the upper bound?");
            upperBound = Double.valueOf(s.nextLine());

            tableFilePath = fileCreate("Please enter the table file location");

        }

        EFieldFileParser parser = new EFieldFileParser();

        Charge[][] chargeArrayArray = parser.parseFile(inputFilePath);
        charges = chargeArrayArray[0];
        positiveCharges = chargeArrayArray[1];
        negativeCharges = chargeArrayArray[2];

        eField = new EField(charges, vAnnode, vCathode, scaleDistance, new Vector(0.0, 0.0, 0.0));

        if (makeGraph) {

            potentialsTable = eField.potentialTable(graphAxis, lowerBound, upperBound, gaps);
            TableGraphWriter tableGraphWriter = new TableGraphWriter();
            String[] headers = {graphAxis+"-coordinate", "Electric Potential"};
            tableGraphWriter.writeCSV(potentialsTable, headers, tableFilePath);
        }
    }

    public void generateFile() {
        positiveFilePath = "";
        negativeFilePath = "";
        outputFilePath = "";

        positiveFilePath = fileNameGet("Please type in the annode (positive) file name");

        negativeFilePath = fileNameGet("Please type cathode (negative) file name");

        outputFilePath = fileCreate("Please enter your output file location");

        System.out.println("How many charges of each polarity do you want?");
        numCharges = Integer.valueOf(s.nextLine());

        System.out.println("What charge are the positive charges?");
        posCharge = Double.valueOf(s.nextLine());
        System.out.println("What charge are the negative charges?");
        negCharge = Double.valueOf(s.nextLine());

        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        scaleDistance = Double.valueOf(s.nextLine());

        System.out.println("How many shake-ups do you want?");
        shakeUps = Integer.valueOf(s.nextLine());

        geometry = new Geometry(positiveFilePath, negCharge, negativeFilePath, posCharge);
        geometry.sumUpSurfaceArea();

        chargeDistributer = new ChargeDistributer(geometry, scaleDistance, numCharges);
        chargeDistributer.balanceCharges(shakeUps);
        charges = chargeDistributer.charges;
        TableGraphWriter writer = new TableGraphWriter();
        writer.writeCSV(charges, outputFilePath);

    }
}
