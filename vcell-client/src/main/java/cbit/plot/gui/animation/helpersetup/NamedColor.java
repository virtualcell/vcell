/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;

import java.awt.*;

public class NamedColor {
    
    private final Color color;
    private final String name;
    
    public NamedColor(String name, Color color){
        this.name = name;
        this.color = color;
    }
    
    public Color getColor(){
        return color;
    }
    
    public String getName(){
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
}
