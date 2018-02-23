/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author jfellows
 */
public class ObjConverter {

    public static ArrayList<TriangleMesh> convert(File file) throws IOException {
        ArrayList<TriangleMesh> meshes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] pieces;
        float[] points = new float[3];
        int[] face = new int[6];
        String[] inds;
        String line;
        int p_offset = 1;

        while ((line = reader.readLine()) != null) {
            pieces = line.split(" ");
            switch (pieces[0]) {
                case "g":
                    if (meshes.size() > 0) {
                        p_offset += meshes.get(meshes.size() - 1).getPoints().size() / 3;
                    }
                    meshes.add(new TriangleMesh());
                    meshes.get(meshes.size() - 1).getTexCoords().addAll(0, 0);
                    break;
                case "v":
                    for (int i = 0; i < 3; i++) {
                        points[i] = Float.parseFloat(pieces[i + 1]);
                    }
                    meshes.get(meshes.size() - 1).getPoints().addAll(points);
                    break;
                case "f":
                    for (int i = 0; i < 3; i++) {
                        inds = pieces[i + 1].split("/");
                        face[i * 2] = Integer.parseInt(inds[0]) - p_offset;
                        face[i * 2 + 1] = 0;
                    }
                    meshes.get(meshes.size() - 1).getFaces().addAll(face);
                    break;
                default:
                    break;
            }
        }
        return meshes;
    }
}
