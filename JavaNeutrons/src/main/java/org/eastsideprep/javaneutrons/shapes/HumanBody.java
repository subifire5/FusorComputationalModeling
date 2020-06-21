/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.shapes;

import org.eastsideprep.javaneutrons.Test;

/**
 *
 * @author gunnar
 */
public class HumanBody extends Shape {

    public HumanBody() {
        super(HumanBody.class.getResource("/body.obj"));
    }
}
