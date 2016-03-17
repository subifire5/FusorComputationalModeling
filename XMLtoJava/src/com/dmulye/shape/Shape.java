package com.dmulye.shape;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Museum containing a name, a city, a permanent exhibition and a special one, these attributes are
 * going to be represented in this order in the generated XML as described in the annotation XmlType
 * 
 * @author dgutierrez-diez
 */
@XmlType( propOrder = { "gap", "point", "radius", "type", "charge" } )
@XmlRootElement( name = "shape" )
public class Shape
{
    Float radius;

    public Float getRadius()
    {
        return radius;
    }

    @XmlElement( name = "radius" )
    public void setRadius( Float radius)
    {
        this.radius = radius;
    }
    
    String charge;
    
    public String getCharge() {
    	return charge;
    }
    
    @XmlElement ( name = "charge" )
    public void setCharge ( String charge ) {
    	this.charge = charge;
    }

    String type;

    public String getType()
    {
        return type;
    }

    @XmlElement( name = "type" )
    public void setType( String type )
    {
        this.type = type;
    }

    List<Point> point;

    public List<Point> getPoint()
    {
        return point;
    }

    @XmlElement( name = "point" )
    public void setPoint( List<Point> point )
    {
        this.point = point;
    }

    List<gap> gap;

    public List<gap> getGap()
    {
        return gap;
    }

    @XmlElement( name = "gap" )
    public void setGap( List<gap> gap )
    {
        this.gap = gap;
    }
    
    @Override
    public String toString()
    {
        StringBuffer str = new StringBuffer( "Charge: " + getCharge() + "\n" + "Type: " + getType() + "\n" +  "Radius: " + getRadius() + "\n" + "Coordinates: " + getPoint() + "\n" + "Gap Point: " + getGap() );
        str.append( "\n" );

        return str.toString();
    }
}
