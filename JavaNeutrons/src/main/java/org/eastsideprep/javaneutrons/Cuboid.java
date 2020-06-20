/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

public class Cuboid extends Shape {

    Cuboid() {

    }

    // create with side lengths
    Cuboid(float dx, float dy, float dz) {
        create(dx, dy, dz);
    }

    // can also make a cube
    Cuboid(float s) {
        create(s, s, s);
    }

    void create(float dx, float dy, float dz) {
        dx /= 2;
        dy /= 2;
        dz /= 2;
        mesh.getPoints().addAll(
                -dx, -dy, dz,
                dx, -dy, dz,
                dx, dy, dz,
                -dx, dy, dz,
                -dx, -dy, -dz,
                dx, -dy, -dz,
                dx, dy, -dz,
                -dx, dy, -dz
        );

        mesh.getTexCoords().addAll(0, 0);

        mesh.getFaces().addAll(
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
                3, 0, 7, 0, 0, 0,
                7, 0, 4, 0, 0, 0,
                //bottom
                4, 0, 6, 0, 5, 0,
                4, 0, 7, 0, 6, 0
        );
    }

}
