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
public class Oxygen extends Element {

    private static Oxygen instance;

    Oxygen() {
        super("Oxygen", 8, 8);
    }

    // we only need one of these objects
    public static synchronized Oxygen getInstance() {
        if (instance == null) {
            Oxygen.instance = new Oxygen();
        }
        return instance;
    }

}
