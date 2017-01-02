/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import Jama.*;

/**
 *
 * @author guberti
 */

/*
    A wire is a method of turning a JSON string into a full grid setup.
    It consists of a single wire that has bent parts and straight parts.
    It starts at a vector and then has a ordered list of bends and straight
    pieces.
*/

public class Wire {
    // All units in mm
    double wireradius;
    Vector start;
    JSONArray bends;
    int charge;
    Vector originalPlane;

    public Wire (String json) {
        MathJSONObject obj = new MathJSONObject(json);
        
        wireradius = obj.getMath("radius");
        charge = obj.getInt("charge");
        
        MathJSONObject jsonStart = new MathJSONObject(obj.getJSONObject("start"));
        start = new Vector(
                jsonStart.getMath("x"),
                jsonStart.getMath("y"),
                jsonStart.getMath("z"),
                jsonStart.getMath("phi"),
                jsonStart.getMath("theta"));
        
        originalPlane = new Vector(
                jsonStart.getMath("planePhi"), 
                jsonStart.getMath("planeTheta"));
        
        bends = obj.getJSONArray("components");       
    }

    public boolean isComponent(String type) {
        String[] componentTypes = {"straight", "bend"};
        for (String componentType : componentTypes) {
            if (type.equals(componentType)) {
                return true;
            }
        }
        return false;
    }

    public List<GridComponent> getAsGridComponents() {        
        List<GridComponent> parts = new ArrayList<>();
        
        MathJSONObject lastObj = new MathJSONObject(); // Needs to be initialized or Netbeans will be sad
        Vector currentPlane = originalPlane;
        System.out.println(originalPlane.toString());
        
        for (int i = 0; i < bends.length(); i++) {
            Vector s;
            MathJSONObject currentObj = new MathJSONObject(bends.getJSONObject(i));
            
            if (i == 0) {
                // If component is the first component in the array,
                // its starting props should be the same as the wire's
                // starting props
                
                s = start;
            } else {
                // Otherwise, the starting props should be equal
                // to the ending props of the last component in the array
                GridComponent lastGC = parts.get(parts.size() - 1);
                
                s = new Vector();
               
                // If it is a straight part
                if ("straight".equals(lastObj.getString("type"))) {
                    
                    double h = lastObj.getMath("height");
                    
                    s.phi = lastGC.pos.phi;
                    s.theta = lastGC.pos.theta;
                    System.out.println("Rotation: " + s.phi + ", " + s.theta);
                    s.x = h * Math.sin(s.theta) * Math.cos(s.phi) + lastGC.pos.x;
                    s.z = h * Math.sin(s.theta) * Math.sin(s.phi) + lastGC.pos.z;
                    
                    // Y is up/down
                    s.y = h * Math.cos(s.theta) + lastGC.pos.y;
                    
                // If it is a curved part
                } else if ("bend".equals(lastObj.getString("type"))) {
                    double initialPhi = ((TorusSegment)(lastGC)).phi2 + 
                            ((TorusSegment)(lastGC)).phi3;
                    
                    double prelimX = lastGC.pos.x + Math.cos(initialPhi) * lastGC.radius;
                    
                    double prelimZ = lastGC.pos.z + Math.sin(initialPhi) * lastGC.radius;
                    
                    Point rotatablePoint = new Point(prelimX, lastGC.pos.y, prelimZ);
                    
                    Point finalPoint = rotatablePoint.rotateAroundVector(lastGC.pos);
                    
                    // Now we must calculate a ray coming off of the torus segment
                    
                    // To do this, we will calculate an initial point coming
                    // off our initally calculated ray, and then we
                    // will rotate the point around lastGC.pos and get a new
                    // point, from which we will calculate the ray again
                    
                    Point initialRayPoint = new Point(
                            Math.cos(initialPhi) + finalPoint.x, 
                            finalPoint.y,
                            Math.sin(initialPhi) + finalPoint.z);
                    System.out.println("Initial point: " + rotatablePoint.toString());
                    Point finalRayPoint = initialRayPoint.rotateAroundVector(lastGC.pos);
                    System.out.println("Final ray point: " + finalPoint.toString());
                    // If stuff is broke, try changing the order we subtract
                    // things here
                    
                    // Understand more things here
                    
                    double rayTheta = Math.atan2(
                            finalRayPoint.z - initialRayPoint.z, 
                            finalRayPoint.x - initialRayPoint.x);
                    
                    double rayPhi = Math.asin(
                            (finalRayPoint.x - initialRayPoint.x)/rayTheta);
                    
                    // We're done now, stick all our stuff into one ray
                    s = new Vector(finalPoint, 0, -rayTheta);
                    
                }
            }
            // Now that we have the ray our part must stem from,
            // we need to again treat torus segments and cylinders differently
            
            if ("straight".equals(currentObj.getString("type"))) {
                Cylinder c = new Cylinder(s, wireradius, currentObj.getMath("height"), charge);
                parts.add(c);
                lastObj = currentObj;
            } else if ("bend".equals(currentObj.getString("type"))) {
                
                // R1 could be anything, but we pick this to make our life down
                // thre road easier
                double r1 = currentObj.getMath("radius");
                
                /* To find the center of our torus, we need to take the cross
                product of the starting vector and a vector perpendicular to the
                current plane, as if these vectors were positionless rays. This
                will give us a vector that is perpendicular to these two vectors,
                */
                
                // Cross products can only be taken of vectors in cartesian form
                Point sRay = s.convertRayToCartesian(r1); 
                Point pRay = currentPlane.convertRayToCartesian(r1);
                
                Point tRay = sRay.crossProduct(pRay);
                
                /*Now we need to set the length of this ray to the radius of
                the torus (the inner radius, not the width (i.e. r1, not r2)).
                Thhere are plenty of ways to do this, but we're going to convert
                the vector into spherical form, change its radius, and then
                convert back.
                
                While it may seem at first glance that we can just use
                tRay, since the lengths of sRay and pRay are both what we
                need the length of tRay to be, this is not the case. When you
                take the cross product of two vectors with lengths a and b, 
                the cross product has a length of ab. This means that we need
                to divide tRay by the radius of the current torus.
                */
                
                tRay.divideByLength(r1);

                /* I used to believe that a lot of complicated things had to
                be done in order to determine the direction the torus segment
                was facing, but this is completely false. Because the torus
                needs to lie inside the current plane, the vector that runs
                through the torus is the same as the vector that runs through
                the current plane. Therefore, we can just use the current plane
                as our torus's direction. Simple!
                
                */
                
                                
                /* TODO: REMOVE THIS BLOCK COMMENT ONCE THIS SECTION WORKS
                
                
                Point cPC = currentPlane.convertRayToCartesian(1);
                double[][] cPArr = {{cPC.x}, {cPC.y}, {cPC.z}}; // Maybe change order?
                Matrix cPMatrix = new Matrix(cPArr);
                
                Vector direction = s;
                direction.rotateAroundVector(currentObj.getMath("angle"), cPMatrix);*/
                
                /* Now, we must define how much "torus" we want - it is a torus
                segment, after all. To do this, we must first calculate the vector
                direction equivalent to "0" on the torus. For a torus
                at the origin, the "0" mark is at the Z axis: i.e. phi: pi/2 and
                theta: pi/2. If our torus is rotated, all we do is add pi/2 to
                both phi and theta to get our 0 angle.
                */
                
                Vector segmentStartSpherical = new Vector(
                        currentPlane.phi + Math.PI/2,
                        currentPlane.theta + Math.PI/2);
                
                /* Now, we need to calculate the angle between this segment start
                and the vector "s". We have a function for this, but it requires
                that both vectors be in cartesian form
                */
                
                // Radius of course does not matter for this conversion
                Point segmentStart = segmentStartSpherical.convertRayToCartesian(r1); 
                
                double angleToStart = sRay.getAngleBetweenVectors(segmentStart);
                
                Vector tP = new Vector(
                        tRay.x + s.x,
                        tRay.y + s.y,
                        tRay.z + s.z,
                        currentPlane.phi,
                        currentPlane.theta);
                
                // Third argument should probably not be s.phi
                TorusSegment tS = new TorusSegment(tP, r1, angleToStart,
                        Math.PI/2/*Change this*/, wireradius, charge);
                parts.add(tS);
                lastObj = currentObj;
            } else if ("planeredef".equals(currentObj.getString("type"))) {
                currentPlane = new Vector(currentObj.getMath("phi"), currentObj.getMath("theta"));
            }
            
            // Plane redefs should not be referenced when calculating points
            if (!"planeredef".equals(currentObj.getString("type"))) {
            }
        }
        
        return parts;
    }
}
