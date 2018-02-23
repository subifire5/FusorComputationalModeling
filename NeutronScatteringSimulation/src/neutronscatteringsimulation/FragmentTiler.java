/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.HashMap;
import javafx.scene.shape.TriangleMesh;

public class FragmentTiler extends Block {
    
    HashMap<Long, Integer> map;

    public FragmentTiler(TriangleMesh mesh, double recursionLevel, double maxBump) {
        super(mesh, recursionLevel, maxBump);
    }

    @Override
    void triangulate(int i0, int i1, int i2, double recursionLevel) {
        int x, y, z;

        if (recursionLevel-- > 0) {
            x = getMidpoint(i0, i1);
            y = getMidpoint(i1, i2);
            z = getMidpoint(i0, i2);

            triangulate(x, y, z, recursionLevel);
            triangulate(i0, x, z, recursionLevel);
            triangulate(i1, y, x, recursionLevel);
            triangulate(i2, z, y, recursionLevel);
            return;
        }
        faces.add(new Face(i0, i1, i2));
    }

    private int getMidpoint(int i0, int i1) {
        if (map == null) {
            map = new HashMap<>();
        }
        long smaller = Math.min(i0, i1);
        long greater = Math.max(i0, i1);
        long key = (smaller << 32) + greater;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        points.add(points.get(i0).midpoint(points.get(i1)));
        map.put(key, points.size() - 1);
        return points.size() - 1;
    }

}
