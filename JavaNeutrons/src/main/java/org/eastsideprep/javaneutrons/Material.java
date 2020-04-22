/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;

/**
 *
 * @author gunnar
 */
public class Material {
    
    private class Component {
        Element e;
        double density; // atoms/(barn*cm)
    }
    
    String name;
    ArrayList<Component> components;
}
