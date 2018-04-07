/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

/**
 *
 * @author jfellows
 */
public interface Tiler {
    public void tile(int i0, int i1, int i2);
    public void setBlock(Block block);
}
