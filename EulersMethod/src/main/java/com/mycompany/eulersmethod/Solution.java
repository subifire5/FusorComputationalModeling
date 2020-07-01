/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.EulersMethod;

/**
 *
 * @author subif
 */
public interface Solution {
    // your solution should probably have a method like "step" in it
    // so you can inherit this interface
    public void step(Particle p, Double stepSize);
}
