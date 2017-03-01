/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author sfreisem-kirov
 */
public class printShapesXML {
    public final static String[] INFO_ORDER = {"x", "y", "z", "phi", "theta"};
    
    public static void printShapes(ArrayList<GridComponent> parts){
    FileOutputStream fos = null;
    
    try {
        fos = new FileOutputStream("test.xml");
        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
        writer.writeStartDocument();
        writer.writeStartElement("root");
        writer.writeStartElement("config");
        writer.writeStartElement("shapes");
        for(GridComponent gc : parts){
            writer.writeStartElement("shape");
            double[] info = new double[5];
            info[0] = gc.pos.x;
            info[1] = gc.pos.y;
            info[2] = gc.pos.z;
            info[3] = gc.pos.phi;
            info[4] = gc.pos.theta;
            for(int i = 0; i < info.length; i++){
                writer.writeStartElement(INFO_ORDER[i]);
                writer.writeCharacters(""+info[i]);
                writer.writeEndElement();
            }
            writer.writeEndElement();
            
            writer.writeEndElement();
            
            writer.writeEndElement();
        }
        
        
        // write stuff
        writer.writeEndElement();
        writer.flush();
    }
    catch(IOException exc) {
    }
    catch(XMLStreamException exc) {
    }
    finally {
    }
    }
}
