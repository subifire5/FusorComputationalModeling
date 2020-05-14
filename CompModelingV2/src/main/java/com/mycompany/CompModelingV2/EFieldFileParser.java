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
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class EFieldFileParser {

    public List<Charge> parseFile(String filePath) {
        Reader reader;
        reader = null;
        List<String[]> rawCharges;

        List<Charge> charges = new ArrayList<>();
        try {
            reader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .build();
            rawCharges = csvReader.readAll();
            reader.close();
            csvReader.close();

            for (String[] s : rawCharges) {
                charges.add(new Charge(s));

            }

        } catch (FileNotFoundException ex) {
            System.out.println(" file not found");
        } catch (IOException ex) {
            System.out.println("io exception");
        }

        return charges;

    }

}
