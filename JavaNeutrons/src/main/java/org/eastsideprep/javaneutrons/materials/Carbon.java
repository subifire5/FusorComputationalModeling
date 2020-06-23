/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.assemblies.Element;
import org.eastsideprep.javaneutrons.core.Util;

/**
 *
 * @author gunnar
 */
public class Carbon extends Element {

    private static Carbon instance;

    Carbon() {
        super("Carbon", 6, 6, 1.9944235e-26);
        readDataFiles("600"); // not sure why we don't have a 625
    }


    // we only need one of these objects
    public static synchronized Carbon getInstance() {
        if (instance == null) {
            Carbon.instance = new Carbon();
        }
        return instance;
    }

}
