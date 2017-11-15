/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.ArrayList;

/**
 *
 * @author jfellows
 */
public class Side {

    int rows;
    int cols;
    ArrayList<Vector3> points;

    Side(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        points = new ArrayList<>();
    }

    void add(Vector3 v) {
        points.add(v);
    }

    Vector3 get(int row, int col) {
        return points.get(col + cols * row);
    }
    
    ArrayList<Face> faces() {
        ArrayList<Face> faces = new ArrayList<>();
        Face f;
        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols - 1; col++) {
                f = new Face(get(row, col).i, get(row, col + 1).i, get(row + 1, col).i);
                faces.add(f);
                faces.add(f.reverse());
                f = new Face(get(row + 1, col).i, get(row, col + 1).i, get(row + 1, col + 1).i);
                faces.add(f);
                faces.add(f.reverse());
            }
        }
        return faces;
    }
}
