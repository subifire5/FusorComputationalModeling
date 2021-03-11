/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.efieldgeneration;

/**
 *
 * @author subif
 */
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TriangleFileParser {

    public Triangle[][] parseFile(String filePath) {
        Reader reader;
        reader = null;
        List<String[]> rawTriangles;
        ArrayList<Triangle> posTriangles = new ArrayList<Triangle>();
        ArrayList<Triangle> negTriangles = new ArrayList<Triangle>();
        Triangle[] triangles = null;
        Triangle[] positiveTriangles = null;
        Triangle[] negativeTriangles = null;
        try {
            reader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .build();
            rawTriangles = csvReader.readAll();
            reader.close();
            csvReader.close();
            triangles = new Triangle[rawTriangles.size()];
            int i = 0;
            for (String[] s : rawTriangles) {
                triangles[i] = (new Triangle(s));
                if (triangles[i].polarity == 1) {
                    posTriangles.add(triangles[i]);
                } else {
                    negTriangles.add(triangles[i]);
                }
                i++;
            }

            positiveTriangles = posTriangles.toArray(triangles);
            negativeTriangles = negTriangles.toArray(triangles);

        } catch (FileNotFoundException ex) {
            System.out.println(" file not found");
        } catch (IOException ex) {
            System.out.println("io exception");
        }
        Triangle[][] TriangleArrayArray = {triangles, positiveTriangles, negativeTriangles};

        return TriangleArrayArray;

    }

    /**
     * Prints the charges into an output file
     *
     * @param triangles
     * @param filePath
     */
    public void writeCSV(Triangle[] triangles, String filePath) {
        FileWriter outputFile = null;
        try {
            File file = new File(filePath);
            file.createNewFile();
            outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            String[] header = {"X1", "Y1", "Z1", "X2", "Y2", "Z2", "X3", "Y3", "Z3", "Polarity", "Surface Area"};
            writer.writeNext(header);
            for (Triangle t : triangles) {
                writer.writeNext(t.toCSVString());
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
