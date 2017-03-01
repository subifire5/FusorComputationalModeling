/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author sfreisem-kirov
 */
public class printShapesXML {
    
    public static void printShapes(){
    FileOutputStream fos = null;
    
    try {
        fos = new FileOutputStream("test.xml");
        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
        writer.writeStartDocument();
        writer.writeStartElement("test");
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
