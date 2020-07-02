/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import javafx.scene.Group;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.core.Assembly;
import org.eastsideprep.javaneutrons.core.Part;
import org.eastsideprep.javaneutrons.core.MonteCarloSimulation;
import org.eastsideprep.javaneutrons.core.Util;
import org.eastsideprep.javaneutrons.core.Shape;
import org.fxyz3d.shapes.primitives.CuboidMesh;

/**
 *
 * @author gunnar
 */
public class TestSV {
    
    public static MonteCarloSimulation simulationTest(Group visualizations){
            //return simpleTest(visualizations);
            //return simpleWaxBlockTest(visualizations);
            return iglooTest(visualizations);
        }
    
    
    
    public static MonteCarloSimulation iglooTest(Group visualizations){
        //shapes for d1 and d2
            Shape detectorShape = new Shape(new CuboidMesh(100, 100, 2));
            detectorShape.getTransforms().add(new Translate(0, 0, -101));  

            //build d1 and d2
            Part d1 = new Part("Detector", detectorShape, "Vacuum");
            
            //get paraffin blocks
            Assembly paraffin = new Assembly("Paraffin", TestSV.class.getResource("/meshes/5mmgaps.obj"), "Paraffin");
            
            Assembly fusor = new Assembly("Fusor");
            fusor.addAll(paraffin, d1);
        
            // make some axes
            // Util.Graphics.drawCoordSystem(visualizations);

           return new MonteCarloSimulation(fusor, Vector3D.ZERO, visualizations);
    }
    
    
    public static MonteCarloSimulation simpleTest(Group visualizations) {

        //shapes for d1, d2, and paraffin
        Shape detectorShape = new Shape(new CuboidMesh(100, 100, 2));
        detectorShape.getTransforms().add(new Translate(0, 0, -101));  
        
        Shape detectorShape2 = new Shape(new CuboidMesh(100, 100, 2));
        detectorShape2.getTransforms().add(new Translate(0, 0, 101));
        
        Shape blockShape = new Shape(new CuboidMesh(100, 100, 25));
        blockShape.getTransforms().add(new Translate(0, 0, 62.5));
        
        //build parts for d1, d2, and paraffin
        Part d1 = new Part("Detector No Shielding", detectorShape, "Vacuum");
        Part d2 = new Part("Detector Post-Shielding", detectorShape2, "Vacuum");
        Part paraffin = new Part("Paraffin Block", blockShape, "Paraffin");
               
        // assemble
        Assembly fusor = new Assembly("Fusor");
        fusor.addAll(paraffin, d1, d2);
        fusor.containsMaterialAt("Vacuum", Vector3D.ZERO);
        
        // make some axes
        Util.Graphics.drawCoordSystem(visualizations);

        return new MonteCarloSimulation(fusor, Vector3D.ZERO, visualizations);    
    }
        
    
     public static MonteCarloSimulation simpleWaxBlockTest(Group visualizations){
            //shapes for d1 and d2
            Shape detectorShape = new Shape(new CuboidMesh(100, 100, 2));
            detectorShape.getTransforms().add(new Translate(0, 0, -101));  
            Shape detectorShape2 = new Shape(new CuboidMesh(100, 100, 2));
            detectorShape2.getTransforms().add(new Translate(0, 0, 101));
            
            //build d1 and d2
            Part d1 = new Part("Detector No Shielding", detectorShape, "Vacuum");
            Part d2 = new Part("Detector Post-Shielding", detectorShape2, "Vacuum");
            
            //get paraffin block
            //Assembly paraffin = new Assembly("Paraffin Block", TestGM.class.getResource("/meshes/flatblock.obj"), "Paraffin");
            //paraffin.getTransforms().add(new Translate(0, 0, 62.5));
            Assembly paraffin = new Assembly("Paraffin Block", TestGM.class.getResource("/meshes/flatblock2.obj"), "Paraffin");
    
            Assembly fusor = new Assembly("Fusor");
           
            fusor.addAll(paraffin, d1, d2);
            fusor.containsMaterialAt("Vacuum", Vector3D.ZERO);
        
            // make some axes
            Util.Graphics.drawCoordSystem(visualizations);

           return new MonteCarloSimulation(fusor, Vector3D.ZERO, visualizations);
    }
        
 
}
