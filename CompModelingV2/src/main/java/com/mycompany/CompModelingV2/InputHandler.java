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
    }
}
