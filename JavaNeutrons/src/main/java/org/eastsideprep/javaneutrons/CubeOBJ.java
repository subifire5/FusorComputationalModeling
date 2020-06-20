/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.net.URISyntaxException;

public class CubeOBJ extends Shape {

    CubeOBJ() throws URISyntaxException {
        super(Test.class.getResource("/cube.obj"));
    }
}
