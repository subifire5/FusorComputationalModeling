/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.materials;

import org.eastsideprep.javaneutrons.core.Nuclide;
import org.eastsideprep.javaneutrons.core.Util;

/**
 *
 * @author gunnar
 */
public class E206Pb extends Nuclide {

    private static E206Pb instance;

    E206Pb() {
        super("206 Pb", 82, 206-82, 205.9744653*Util.Physics.Da);
    }

    // we only need one of these objects
    public static synchronized E206Pb getInstance() {
        if (instance == null) {
            E206Pb.instance = new E206Pb();
        }
        return instance;
    }

}
