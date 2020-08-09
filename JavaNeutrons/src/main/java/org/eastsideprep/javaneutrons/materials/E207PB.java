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
public class E207Pb extends Nuclide {

    private static E207Pb instance;

    E207Pb() {
        super("207 Pb", 82, 207-82, 206.9758969*Util.Physics.Da);
    }

    // we only need one of these objects
    public static synchronized E207Pb getInstance() {
        if (instance == null) {
            E207Pb.instance = new E207Pb();
        }
        return instance;
    }

}
