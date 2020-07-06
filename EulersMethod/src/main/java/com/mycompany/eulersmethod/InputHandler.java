/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/* MAGGIE AND PRAVEER:
* in the class inputHandler, there's a function called "orbitStuff"
* to have your step code run with user input etc,
* just uncomment the section which has your initials in it
 */
/**
 *
 * @author subif
 */
import java.io.File;
import java.util.Random;
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
        s = new Scanner(System.in);
        readFromFile();

        s.close();
    }

    /**
     *
     * @param PJ
     * @param MY
     * @param initial
     * @param numberOfSteps
     * @param stepSize
     * @param outputFilePath
     * @return
     */
    public void orbitStuff(Boolean PJ, Boolean MY, Particle initial, int numberOfSteps, Double stepSize, String outputFilePath) {
        Particle[] particles = new Particle[numberOfSteps];
        if (PJ) {
            
            PJEulersMethod pj = new PJEulersMethod(eField);
            particles[0] = pj.step(initial, stepSize);
            Particle p = particles[0];
            for (int i = 1; i < numberOfSteps; i++) {
                particles[i] = pj.step(p, stepSize);
                p = particles[i];
            }
             
        } else if (MY) {

            MYEulersMethod my = new MYEulersMethod(eField);
            particles[0] = my.step(initial, stepSize);
            Particle p = particles[0];
            for (int i = 1; i < numberOfSteps; i++) {
                particles[i] = my.step(p, stepSize);
                p = particles[i];
            }

        }

        String[] headers = {"X", "Y", "Z", "Vx", "Vy", "Vz", "Polarity", "Time", "Mass"};

        TableGraphWriter tgw = new TableGraphWriter();
        tgw.writeCSV(particles, headers, outputFilePath);

    }

    public Particle randomParticle() {
        Random r = new Random();
        Vector position = new Vector(r.nextDouble(), r.nextDouble(), r.nextDouble());
        Vector velocity = new Vector(r.nextDouble(), r.nextDouble(), r.nextDouble());
        Particle p = new Particle(position, velocity, r.nextInt(), r.nextDouble());
        return p;
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

    Particle getInitialParticle() {
        System.out.println("Initial Position (recommended >-80 and <80 \n" + " x: ");
        Double x = Double.valueOf(s.nextLine());

        System.out.println("y: ");
        Double y = Double.valueOf(s.nextLine());

        System.out.println("z: ");
        Double z = Double.valueOf(s.nextLine());

        System.out.println("Initial Velocity \n" + " Vx: ");
        Double Vx = Double.valueOf(s.nextLine());

        System.out.println("Vy: ");
        Double Vy = Double.valueOf(s.nextLine());

        System.out.println("Vz: ");
        Double Vz = Double.valueOf(s.nextLine());

        System.out.println("Polarity: ");
        int polarity = Integer.valueOf(s.nextLine());
        System.out.println("To denote a number several decimals below the ones position,"
                + "\n use the following 2.014E-27, where -27 is the number of,"
                + "\n digits behind the ones position that the number starts");
        System.out.println("Intital Time (seconds): ");
        Double time = Double.valueOf(s.nextLine());

        System.out.println("Mass (in Atomic Mass Units, for Deuterium this is 2.0141)");
        Double mass = Double.valueOf(s.nextLine()) * 1.66E-27;

        return new Particle(x, y, z, Vx, Vy, Vz, polarity, time, mass);
    }

    public void readFromFile() {
        Boolean PJ = false;
        Boolean MY = false;
        Particle initial = null;
        boolean inputRecieved = false;
        int numberOfSteps = 0;
        Double stepSize = 1.0;
        String outputFilePath = "";

        inputFilePath = fileNameGet("Please enter your input (including the .csv): ");

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
        Boolean calculateOrbit = false;
        input = "";
        inputRecieved = false;
        while (!inputRecieved) {
            System.out.println("Do you want to calculate an orbit?(Y/N)");

            input = s.nextLine();
            if (input.equals("Y") || input.equals("y")) {
                inputRecieved = true;
                calculateOrbit = true;
            } else if (input.equals("N") || input.equals("n")) {
                inputRecieved = true;
                calculateOrbit = false;
            } else {
                System.out.println("Please respond with (Y) or (N)");
            }

        }
        inputRecieved = false;
        input = "";
        if (calculateOrbit) {
            initial = getInitialParticle();

            outputFilePath = fileCreate("Please enter the orbit output file name \n"
                    + " (including extensions like .csv): ");
            System.out.println("Step Size: ");
            stepSize = Double.valueOf(s.nextLine());

            System.out.println("Number of Steps:");
            numberOfSteps = Integer.valueOf(s.nextLine());

            inputRecieved = false;

            while (!inputRecieved) {
                System.out.println("Who's Orbit do you want? Praveer (PJ) Maggie (MY)");
                input = s.nextLine();
                if (input.equals("PJ") || input.equals("Pj") || input.equals("pJ") || input.equals("pj")) {
                    inputRecieved = true;
                    PJ = true;
                } else if (input.equals("MY") || input.equals("My") || input.equals("mY") || input.equals("my")) {
                    inputRecieved = true;
                    MY = true;
                } else {
                    System.out.println("Please respond with (PJ) or (MY)");
                }
            }

            orbitStuff(PJ, MY, initial, numberOfSteps, stepSize, outputFilePath);
        }

        inputRecieved = false;
        input = "";

    }

}
