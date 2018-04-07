/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.HashMap;

public class FragmentTiler implements Tiler {

    HashMap<Long, Integer> map;
    final int RECURSION_LEVEL;
    Block block;

    public FragmentTiler(int RECURSION_LEVEL) {
        this.RECURSION_LEVEL = RECURSION_LEVEL;
    }
    
    @Override
    public void setBlock(Block block) {
        this.block = block;
        if (map == null) {
            map = new HashMap<>();
        } else {
            map.clear();
        }
    }

    @Override
    public void tile(int i0, int i1, int i2) {
        int level = RECURSION_LEVEL;
        _tile(i0, i1, i2, level);
    }

    private void _tile(int i0, int i1, int i2, int level) {
        int x, y, z;

        if (level-- > 0) {
            x = getMidpoint(i0, i1);
            y = getMidpoint(i1, i2);
            z = getMidpoint(i0, i2);

            _tile(x, y, z, level);
            _tile(i0, x, z, level);
            _tile(i1, y, x, level);
            _tile(i2, z, y, level);
            return;
        }
        block.faces.add(block.new Face(i0, i1, i2));
    }

    private int getMidpoint(int i0, int i1) {
        long smaller = Math.min(i0, i1);
        long greater = Math.max(i0, i1);
        long key = (smaller << 32) + greater;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        block.points.add(block.points.get(i0).midpoint(block.points.get(i1)));
        map.put(key, block.points.size() - 1);
        return block.points.size() - 1;
    }
}
