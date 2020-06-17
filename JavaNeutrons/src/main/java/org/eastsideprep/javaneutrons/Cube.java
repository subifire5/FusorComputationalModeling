/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

public class Cube extends Shape {

    Cube(float s) {
        getPoints().addAll(
                0, 0, s,
                s, 0, s,
                s, s, s,
                0, s, s,
                0, 0, 0,
                s, 0, 0,
                s, s, 0,
                0, s, 0
        );

        getTexCoords().addAll(0, 0);

        getFaces().addAll(
                // top
                0, 0, 1, 0, 2, 0,
                0, 0, 2, 0, 3, 0,
                //front
                0, 0, 4, 0, 1, 0,
                4, 0, 5, 0, 1, 0,
                //right
                1, 0, 5, 0, 2, 0,
                5, 0, 6, 0, 2, 0,
                //back
                2, 0, 6, 0, 3, 0,
                6, 0, 7, 0, 3, 0,
                //left
                3, 0, 7, 0, 4, 0,
                7, 0, 0, 0, 4, 0,
                //bottom
                4, 0, 5, 0, 6, 0,
                4, 0, 6, 0, 7, 0
        );
    }
}
