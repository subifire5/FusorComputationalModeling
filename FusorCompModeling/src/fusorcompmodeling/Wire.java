/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import matheval.MathEval;
import org.json.JSONArray;
import org.json.JSONObject;

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
                    s.x = h * Math.sin(s.phi) * Math.cos(s.theta) + lastGC.pos.x;
                    s.z = h * Math.sin(s.phi) * Math.sin(s.theta) + lastGC.pos.z;
                    
                    // Y is up/down
                    s.y = h * Math.cos(s.phi) + lastGC.pos.y;
                    
                // If it is a curved part
                } else if ("bend".equals(lastObj.getString("type"))) {
                    double initialPhi = ((TorusSegment)(lastGC)).phi2 + 
                            ((TorusSegment)(lastGC)).phi3;
                    
                    double prelimX = lastGC.pos.x * Math.cos(initialPhi) - 
                            lastGC.pos.z * Math.sin(initialPhi);
                    
                    double prelimZ = lastGC.pos.z * Math.cos(initialPhi) + 
                            lastGC.pos.x * Math.sin(initialPhi);
                    
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
                    
                    Point finalRayPoint = initialRayPoint.rotateAroundVector(lastGC.pos);
                    
                    // If stuff is broke, try changing the order we subtract
                    // things here
                    
                    // Understand more things here
                    
                    double rayTheta = Math.atan2(
                            finalRayPoint.z - initialRayPoint.z, 
                            finalRayPoint.x - initialRayPoint.x);
                    
                    double rayPhi = Math.asin(
                            (finalRayPoint.x - initialRayPoint.x)/rayTheta);
                    
                    // We're done now, stick all our stuff into one ray
                    s = new Vector(finalPoint, rayPhi, rayTheta);
                    
                    
                }
            }
            // Now that we have the ray our part must stem from,
            // we need to again treat torus segments and cylinders differently
            
            if ("straight".equals(currentObj.getString("type"))) {
                Cylinder c = new Cylinder(s, wireradius, currentObj.getMath("height"), charge);
                parts.add(c);
                lastObj = currentObj;
            } else if ("bend".equals(currentObj.getString("type"))) {
                // First we must convert our spherical coordinates to get
                // a point, and then take its cross product with the current plane
                double r1 = currentObj.getMath("radius");
                Point sRay = s.convertRayToCartesian(r1);
                Point pRay = currentPlane.convertRayToCartesian(r1);
                Point tRay = sRay.crossProduct(pRay);
                tRay.divideByLength(sRay.getLength());
                
                Vector tP = new Vector(
                        tRay.x + s.x,
                        tRay.y + s.y,
                        tRay.z + s.z,
                        currentPlane.phi, // Probably wrong
                        currentPlane.theta); // Also fix here
                
                // Third argument should probably not be s.phi
                TorusSegment tS = new TorusSegment(tP, r1, s.phi,
                        currentObj.getMath("angle"), wireradius, charge);
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
