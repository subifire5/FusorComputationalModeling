package com.dmulye.shape;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JaxB is not capable of marshal lists directly as root elements, so we need a container for the
 * list of museums. Getter and Setter are used by jaxb
 * 
 * @author dgutierrez-diez
 */
@XmlRootElement( name = "shapes" )
public class Shapes
{
    List<Shape> shapes;

    public List<Shape> getShapes()
    {
        return shapes;
    }

    /**
     * element that is going to be marshaled in the xml
     */
    @XmlElement( name = "Shape" )
    public void setShapes( List<Shape> shapes )
    {
        this.shapes = shapes;
    }

    /**
     * This method is not used by jaxb, just used for business reasons. In the case that this class
     * would be generated using xml schemas definitions, this method has to be added to the
     * generated class or to some helper or util one
     * 
     * @param museum
     */
    public void add( Shape shape )
    {
        if( this.shapes == null )
        {
            this.shapes = new ArrayList<Shape>();
        }
        this.shapes.add( shape );

    }

    @Override
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        for( Shape shape : this.shapes )
        {
            str.append( shape.toString() );
        }
        return str.toString();
    }

}


