package com.dmulye.shape;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;



/**
 * Exhibition containing a name, a date range and a list of artists the xml order is configured
 * using the XmlType annotation, the node name is defined in the name attribute of the
 * XmlRootElement or XmlElement
 * 
 * @author dgutierrez-diez
 */
// field name and not xml element name
@XmlType( propOrder = { "x", "y", "z" } )
@XmlRootElement( name = "gap" )
public class gap
{

    Float       x;

    Float    y;

    Float    z;

    public Float getX()
    {
        return x;
    }

    // name to be used in the xml
    @XmlElement( name = "x" )
    public void setX( Float x )
    {
        this.x = x;
    }

    public Float getY()
    {
        return y;
    }

    // name to be used in the xml
    @XmlElement( name = "y" )
    public void setY( Float y )
    {
        this.y = y;
    }
    
    public Float getZ()
    {
        return z;
    }

    // name to be used in the xml
    @XmlElement( name = "z" )
    public void setZ( Float z )
    {
        this.z = z;
    }

    @Override
    public String toString()
    {
    	return "Gap Point: (" + getX() + ", " + getY() + ", " + getZ() + ")\n";
    }
    
    public float[] createArray() {
    	float[] array = {getX(), getY(), getZ()};
    	return array;
    }
}


