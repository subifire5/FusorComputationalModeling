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

import com.opencsv.CSVWriter;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class EFieldFileWriter {
    List<Charge> charges;
    File file;
    public EFieldFileWriter(List<Charge> charges){
        this.charges = charges;
    }
    
    public EFieldFileWriter(ChargeDistributer chargeDistributer){
        this.charges = chargeDistributer.charges;
    }


    public void writeCSV(String filePath){
        FileWriter outputFile = null;
        try {
            File file = new File(filePath);
            outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            
            String[] header = {"X", "Y", "Z", "Polarity"};
            writer.writeNext(header);
            for(Charge c: charges){
                writer.writeNext(c.toCSVString());
            }
            
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(); // try catch for file stuff
        } finally {
            try {
                outputFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
    
    }
    
    
    
}
