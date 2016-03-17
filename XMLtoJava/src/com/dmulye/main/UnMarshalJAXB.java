package com.dmulye.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;

import com.dmulye.modeling.cylinderTest;
import com.dmulye.shape.Cylinder;
import com.dmulye.shape.Shapes;
import com.dmulye.shape.Torus;

public class UnMarshalJAXB
{
    public static List<Torus> torus = new ArrayList<Torus>();
    public static List<Cylinder> cylinder = new ArrayList<Cylinder>();
    
    public List<Torus> getTorus() {
    	return torus;
    }
    
    public List<Cylinder> getCylinder() {
    	return cylinder;
    }
    
    public static void main( String[] args )
    {
    	
        try
        {
            File file = new File( "cage2.xml" );
            JAXBContext jaxbContext = JAXBContext.newInstance( Shapes.class );

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Shapes shapes = (Shapes)jaxbUnmarshaller.unmarshal( file );
            int size = shapes.getShapes().size();
            for ( int i = 0; i < size; i++ ) {
            	if(shapes.getShapes().get(i).getType().equals("Torus")) {
            		Torus temp = new Torus(
            		shapes.getShapes().get(i).getRadius(), 
            		shapes.getShapes().get(i).getType(), 
            		shapes.getShapes().get(i).getPoint().get(0).createArray(), 
            		shapes.getShapes().get(i).getPoint().get(1).createArray(), 
            		shapes.getShapes().get(i).getPoint().get(2).createArray(),
            		shapes.getShapes().get(i).getCharge(), 
            		shapes.getShapes().get(i).getGap().get(0).createArray(), 
            		shapes.getShapes().get(i).getGap().get(1).createArray());
            		torus.add(temp);
            	} else if(shapes.getShapes().get(i).getType().equals("Cylinder")) {
            		Cylinder temp1 = new Cylinder(
            				shapes.getShapes().get(i).getRadius(),
            				shapes.getShapes().get(i).getType(), 
            				shapes.getShapes().get(i).getPoint().get(0).createCylinderArray(),
            				shapes.getShapes().get(i).getPoint().get(1).createCylinderArray());
            		cylinder.add(temp1);
            	}
            }
           System.out.println(torus.toString());
           System.out.println(cylinder.toString());
        }
        catch( JAXBException e )
        {
            e.printStackTrace();
        }
        
        

    }

}
