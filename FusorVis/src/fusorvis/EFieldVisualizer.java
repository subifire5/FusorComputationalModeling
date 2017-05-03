/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;

import fusorcompmodeling.*;
import java.util.Arrays;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

/**
 *
 * @author guberti
 */
public class EFieldVisualizer {
    int aW; // Array width
    int aH; // Array height
    
    double wU; // Width unit
    double hU; // Height unit
    
    double sW; // Slice width
    double sH; // Slice height
    
    int blockSideLength;
    PixelWriter w;
    Point[] points;
    
    
    public EFieldVisualizer(double sliceWidth, double sliceHeight, double cF, int blockSideLength, PixelWriter w, Point[] points) {
        System.out.println(EField.kQ);
        this.aW = (int) ((int) sliceWidth * cF / blockSideLength);
        this.aH = (int) ((int) sliceHeight * cF / blockSideLength);

        this.wU = sliceWidth / this.aW;
        this.hU = sliceHeight / this.aH;
        
        this.sW = sliceWidth;
        this.sH = sliceHeight;
        
        this.points = points;
        this.blockSideLength = blockSideLength;
        this.w = w;
    }
    
    
    
    public double[][][] calcSlice(Rotate[] eFieldTransforms, Box eFieldSlice) {

        double[][][] fieldGrid = new double[aW][aH][3];
        
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                Point p = new Point((-(sW / 2) + i * wU), (-(sH / 2) + k * hU), 0);
                p = translateEFieldPixel(p, eFieldTransforms, eFieldSlice);
                
                Vector efield = EField.EFieldSum(points, p);
                
                fieldGrid[i][k][0] = efield.x;
                fieldGrid[i][k][1] = efield.y;
                fieldGrid[i][k][2] = efield.z;
            }
        }
        
        return fieldGrid;
    }
    
    public void renderRGBCodedVectors(Rotate[] eFieldTransforms, Box eFieldSlice) {
        
        double[][][] fieldGrid = calcSlice(eFieldTransforms, eFieldSlice);
        
        double[][] sorted = new double[3][aW * aH];
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                for (int j = 0; j < 3; j++) {
                    sorted[j][k * aW + i] = fieldGrid[i][k][j];
                    
                }
            }
        }
        
        for (int j = 0; j < 3; j++) {
            Arrays.sort(sorted[j]);
        }

        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                Color c = new Color(
                        toColor(indexOf(sorted[0], fieldGrid[i][k][0]), sorted[0].length),
                        toColor(indexOf(sorted[1], fieldGrid[i][k][1]), sorted[1].length),
                        toColor(indexOf(sorted[2], fieldGrid[i][k][2]), sorted[2].length),
                        1.0);
                
                drawBlock(i, k, c);
            }
        }
    }
    
    public void renderIntensities(Rotate[] eFieldTransforms, Box eFieldSlice) {
        double[][][] fG = calcSlice(eFieldTransforms, eFieldSlice);
        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        double[][] intens = new double[aW][aH];
        
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                intens[i][k] = Math.sqrt(Math.pow(fG[i][k][0], 2) + Math.pow(fG[i][k][1], 2) + Math.pow(fG[i][k][2], 2));
                min = Math.min(min, intens[i][k]);
                max = Math.max(max, intens[i][k]);
            }
        }
        
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                double gV = (intens[i][k] - min) / (max - min);
                
                Color c = new Color(gV, gV, gV, 1.0);
                
                drawBlock(i, k, c);
            }
        }   
    }
    
    public void renderIntensitiesContrasted(Rotate[] eFieldTransforms, Box eFieldSlice) {
        double[][][] fG = calcSlice(eFieldTransforms, eFieldSlice);
        
        double[] sorted = new double[aW * aH];
        double[][] monocolored = new double[aW][aH];
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                for (int j = 0; j < 3; j++) {
                    double val = Math.sqrt(Math.pow(fG[i][k][0], 2) + Math.pow(fG[i][k][1], 2) + Math.pow(fG[i][k][2], 2));
                    sorted[k * aW + i] = val;
                    monocolored[i][k] = val;                    
                }
            }
        }
        for (double item : sorted) {
            if (item < 0) {
                System.out.println("Negative!");
            }
        }
        Arrays.sort(sorted);
                
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                double gV = toColor(indexOf(sorted, monocolored[i][k]), sorted.length);
                
                Color c = new Color(gV, gV, gV, 1.0);
                
                drawBlock(i, k, c);
            }
        }
    }
    
    public void renderElectricPotential(Rotate[] eFieldTransforms, Box eFieldSlice) {
        double[][] fieldGrid = new double[aW][aH];
        double[] sorted = new double[aW * aH];

        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                Point p = new Point((-(sW / 2) + i * wU), (-(sH / 2) + k * hU), 0);
                p = translateEFieldPixel(p, eFieldTransforms, eFieldSlice);
                
                fieldGrid[i][k] = PointDistributer.electricPotential(points, p);
                sorted[k * aW + i] = fieldGrid[i][k];
            }
        }

        Arrays.sort(sorted);
                
        for (int i = 0; i < aW; i++) {
            for (int k = 0; k < aH; k++) {
                double gV = toColor(indexOf(sorted, fieldGrid[i][k]), sorted.length);
                
                Color c = new Color(gV, 0.0, 1.0 - gV, 1.0);
                
                drawBlock(i, k, c);
            }
        }
    }
    
    private void drawBlock(int i, int k, Color c) {
        for (int j = 0; j < blockSideLength; j++) {
            for (int l = 0; l < blockSideLength; l++) {
                w.setColor(i * blockSideLength + j, k * blockSideLength + l, c);
            }
        }
    }
    private double toColor(int index, int length) {
        // Return a val between 0.0 and 1.0
        return (double) index / (double) (length - 1);
    }
    
    private void reverse(double[][] arrs) {
        for (double[] arr : arrs) {
            //ArrayUtils.reverse(arr);
        }
    }
    
    private int indexOf(double[] arr, double item) {
        // Simple binary search
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (item < arr[mid]) {
                high = mid - 1;
            } else if (item > arr[mid]) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return -1; // Will never get here
    }
    
    public Point translateEFieldPixel(Point p, Rotate[] eFieldTransforms, Box eFieldSlice) {
        Point r = new Point(p.x, p.y, p.z);
        double c = 1;

        r.y = r.y * Math.cos(eFieldTransforms[0].getAngle() * c) - r.z * Math.sin(eFieldTransforms[0].getAngle() * c);
        r.z = r.y * Math.sin(eFieldTransforms[0].getAngle() * c) + r.z * Math.cos(eFieldTransforms[0].getAngle() * c);

        r.z = r.z * Math.cos(eFieldTransforms[1].getAngle() * c) - r.x * Math.sin(eFieldTransforms[1].getAngle() * c);
        r.x = r.z * Math.sin(eFieldTransforms[1].getAngle() * c) + r.x * Math.cos(eFieldTransforms[1].getAngle() * c);

        r.x = r.x * Math.cos(eFieldTransforms[2].getAngle() * c) - r.y * Math.sin(eFieldTransforms[2].getAngle() * c);
        r.y = r.x * Math.sin(eFieldTransforms[2].getAngle() * c) + r.y * Math.cos(eFieldTransforms[2].getAngle() * c);

        assert !Double.isNaN(r.x);

        Vector v = r.convertToSphericalCoordsExc();
        v.length *= eFieldSlice.getScaleX();

        Point t = v.convertRayToCartesian(v.length);

        assert !Double.isNaN(r.x);

        t.x += eFieldSlice.getTranslateX();
        t.y += eFieldSlice.getTranslateY();
        t.z += eFieldSlice.getTranslateZ();

        return t;
    }
    
}
