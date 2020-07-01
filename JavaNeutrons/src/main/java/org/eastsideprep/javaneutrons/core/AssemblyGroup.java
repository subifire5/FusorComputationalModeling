/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons.core;

import javafx.scene.Group;

/**
 *
 * @author gunnar
 */
public class AssemblyGroup extends Group{
    public Assembly assembly;
    
    public AssemblyGroup(Assembly assembly) {
        this.assembly = assembly;
    }
}
