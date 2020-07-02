/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

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
    Scanner s;
    EField eField;
    Charge[] charges;
    Charge[] positiveCharges;
    Charge[] negativeCharges;

    public InputHandler() {
    }
    

    public void getInput() {

        readFromFile();

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


    public void readFromFile() {

        boolean inputRecieved = false;
        inputFilePath = fileNameGet("Please enter your input file location");

        System.out.println("Please enter the anode (positive) voltage");
        vAnnode = Double.valueOf(s.nextLine());

        System.out.println("Please enter the cathode (negative) voltage");
        vCathode = Double.valueOf(s.nextLine());

        System.out.println("What is the distance, in meters, of 1 unit in the stl files?");
        scaleDistance = Double.valueOf(s.nextLine());

        String input = "";
        inputRecieved = false;

        EFieldFileParser parser = new EFieldFileParser();

        Charge[][] chargeArrayArray = parser.parseFile(inputFilePath);
        charges = chargeArrayArray[0];
        positiveCharges = chargeArrayArray[1];
        negativeCharges = chargeArrayArray[2];

        eField = new EField(charges, vAnnode, vCathode, scaleDistance, new Vector(0.0, 0.0, 0.0));

    }

}
