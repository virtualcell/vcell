/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;


import javax.vecmath.Color3f;
import java.awt.*;
import java.util.HashMap;

public class Colors {
    
    // Need 24 named colors
    public final static String REDSTRING = "RED"; // 1
    public final static String BLUESTRING = "BLUE";
    public final static String LIMESTRING = "LIME";
    public final static String ORANGESTRING = "ORANGE";
    public final static String CYANSTRING = "CYAN"; // 5
    public final static String MAGENTASTRING = "MAGENTA";
    public final static String PINKSTRING = "PINK";
    public final static String YELLOWSTRING = "YELLOW";
    public final static String GRAYSTRING = "GRAY";
    
    public final static String PURPLESTRING = "PURPLE"; //10
    public final static String GREENSTRING = "GREEN";
    public final static String MAROONSTRING = "MAROON";
    public final static String NAVYSTRING = "NAVY";
    public final static String OLIVESTRING = "OLIVE";
    public final static String TEALSTRING = "TEAL"; // 15
    public final static String LIMEGREENSTRING = "LIME_GREEN";
    public final static String GOLDSTRING = "GOLD";
    public final static String DARKGREENSTRING = "DARK_GREEN";
    public final static String CRIMSONSTRING = "CRIMSON";
    public final static String DARKVIOLETSTRING = "DARK_VIOLET"; //20
    public final static String VIOLETSTRING = "VIOLET"; 
    public final static String SLATEBLUESTRING = "SLATE_BLUE";
    public final static String LIGHTCYANSTRING = "LIGHT_CYAN";
    public final static String DARKCYANSTRING = "DARK_CYAN";
    
    public final static String LIGHTGRAYSTRING = "LIGHT_GRAY";
    public final static String DARKGRAYSTRING = "DARK_GRAY";
    public final static String WHITESTRING = "WHITE";
    public final static String BLACKSTRING = "BLACK";
    
    public final static String [] COLORNAMES = {REDSTRING, BLUESTRING,
        LIMESTRING, ORANGESTRING, CYANSTRING, MAGENTASTRING, PINKSTRING, 
        YELLOWSTRING, GRAYSTRING, PURPLESTRING, GREENSTRING, MAROONSTRING,
        NAVYSTRING, OLIVESTRING, TEALSTRING, LIMEGREENSTRING, GOLDSTRING,
        DARKGREENSTRING, CRIMSONSTRING, DARKVIOLETSTRING, VIOLETSTRING, SLATEBLUESTRING,
        LIGHTCYANSTRING, DARKCYANSTRING, LIGHTGRAYSTRING, DARKGRAYSTRING,
        WHITESTRING, BLACKSTRING};
    
    public final static NamedColor RED = new NamedColor(REDSTRING, Color.RED);
    public final static NamedColor BLUE = new NamedColor(BLUESTRING, Color.BLUE);
    public final static NamedColor LIME = new NamedColor(LIMESTRING, Color.GREEN);
    public final static NamedColor ORANGE = new NamedColor(ORANGESTRING, Color.ORANGE);
    public final static NamedColor CYAN = new NamedColor(CYANSTRING, Color.CYAN);
    public final static NamedColor MAGENTA = new NamedColor(MAGENTASTRING, Color.MAGENTA);
    public final static NamedColor PINK = new NamedColor(PINKSTRING, Color.PINK);
    public final static NamedColor YELLOW = new NamedColor(YELLOWSTRING, Color.YELLOW);
    public final static NamedColor GRAY = new NamedColor(GRAYSTRING, Color.GRAY);
    public final static NamedColor PURPLE = new NamedColor(PURPLESTRING, new Color(128, 0, 128));
    public final static NamedColor GREEN = new NamedColor(GREENSTRING, new Color(0, 128, 0));
    public final static NamedColor MAROON = new NamedColor(MAROONSTRING, new Color(128, 0, 0));
    public final static NamedColor NAVY = new NamedColor(NAVYSTRING, new Color(0, 0, 128));
    public final static NamedColor OLIVE = new NamedColor(OLIVESTRING, new Color(128, 128, 0));
    public final static NamedColor TEAL = new NamedColor(TEALSTRING, new Color(0, 128, 128));
    public final static NamedColor LIMEGREEN = new NamedColor(LIMEGREENSTRING, new Color(50, 205, 50));
    public final static NamedColor GOLD = new NamedColor(GOLDSTRING, new Color(255, 215, 0));
    public final static NamedColor DARKGREEN = new NamedColor(DARKGREENSTRING, new Color(0, 100, 0));
    public final static NamedColor CRIMSON = new NamedColor(CRIMSONSTRING, new Color(220, 20, 60));
    public final static NamedColor DARKVIOLET = new NamedColor(DARKVIOLETSTRING, new Color(148, 0, 211));
    public final static NamedColor VIOLET = new NamedColor(VIOLETSTRING, new Color(238, 130, 238));
    public final static NamedColor SLATEBLUE = new NamedColor(SLATEBLUESTRING, new Color(106, 90, 205));
    public final static NamedColor LIGHTCYAN = new NamedColor(LIGHTCYANSTRING, new Color(224, 255, 255));
    public final static NamedColor DARKCYAN = new NamedColor(DARKCYANSTRING, new Color(0, 139, 139));
   
    public final static NamedColor LIGHTGRAY = new NamedColor(LIGHTGRAYSTRING, Color.LIGHT_GRAY);
    public final static NamedColor DARKGRAY = new NamedColor(DARKGRAYSTRING, Color.DARK_GRAY);
    public final static NamedColor WHITE = new NamedColor(WHITESTRING, Color.WHITE);
    public final static NamedColor BLACK = new NamedColor(BLACKSTRING, Color.BLACK);
    
    public final static NamedColor [] COLORARRAY = new NamedColor[]{RED, BLUE,
        LIME, ORANGE, CYAN, MAGENTA, PINK, YELLOW, GRAY, PURPLE, GREEN,
        MAROON, NAVY, OLIVE, TEAL, LIMEGREEN, GOLD, DARKGREEN, CRIMSON,
        DARKVIOLET, VIOLET, SLATEBLUE, LIGHTCYAN, DARKCYAN, LIGHTGRAY,
        DARKGRAY, WHITE, BLACK};
    
    public final static Color3f RED3D = new Color3f(Color.RED);
    public final static Color3f BLUE3D = new Color3f(Color.BLUE);
    public final static Color3f LIME3D = new Color3f(LIME.getColor());
    public final static Color3f ORANGE3D = new Color3f(Color.ORANGE);
    public final static Color3f CYAN3D = new Color3f(Color.CYAN);
    public final static Color3f MAGENTA3D = new Color3f(Color.MAGENTA);
    public final static Color3f PINK3D = new Color3f(Color.PINK);
    public final static Color3f YELLOW3D = new Color3f(Color.YELLOW);
    public final static Color3f GRAY3D = new Color3f(Color.GRAY);
    public final static Color3f PURPLE3D = new Color3f(PURPLE.getColor());
    public final static Color3f GREEN3D = new Color3f(GREEN.getColor());
    public final static Color3f MAROON3D = new Color3f(MAROON.getColor());
    public final static Color3f NAVY3D = new Color3f(NAVY.getColor());
    public final static Color3f OLIVE3D = new Color3f(OLIVE.getColor());
    public final static Color3f TEAL3D = new Color3f(TEAL.getColor());
    public final static Color3f LIMEGREEN3D = new Color3f(LIMEGREEN.getColor());
    public final static Color3f GOLD3D = new Color3f(GOLD.getColor());
    public final static Color3f DARKGREEN3D = new Color3f(DARKGREEN.getColor());
    public final static Color3f CRIMSON3D = new Color3f(CRIMSON.getColor());
    public final static Color3f DARKVIOLET3D = new Color3f(DARKVIOLET.getColor());
    public final static Color3f VIOLET3D = new Color3f(VIOLET.getColor());
    public final static Color3f SLATEBLUE3D = new Color3f(SLATEBLUE.getColor());
    public final static Color3f LIGHTCYAN3D = new Color3f(LIGHTCYAN.getColor());
    public final static Color3f DARKCYAN3D = new Color3f(DARKCYAN.getColor());
    
    public final static Color3f LIGHTGRAY3D = new Color3f(Color.LIGHT_GRAY);
    public final static Color3f DARKGRAY3D = new Color3f(Color.DARK_GRAY);
    public final static Color3f WHITE3D = new Color3f(Color.WHITE);
    public final static Color3f BLACK3D = new Color3f(Color.BLACK);
    
    public final static Color3f [] COLOR3FARRAY = {RED3D, BLUE3D, LIME3D, 
        ORANGE3D, CYAN3D, MAGENTA3D, PINK3D, YELLOW3D, GRAY3D, PURPLE3D,
        GREEN3D, MAROON3D, NAVY3D, OLIVE3D, TEAL3D, LIMEGREEN3D, GOLD3D,
        DARKGREEN3D, CRIMSON3D, DARKVIOLET3D, VIOLET3D, SLATEBLUE3D, 
        LIGHTCYAN3D, DARKCYAN3D, LIGHTGRAY3D, DARKGRAY3D, WHITE3D, BLACK3D};
  
    public final static HashMap<String, Color3f> COLORMAP3D = new HashMap<>(100);
    static{
        for(int i=0;i<COLORNAMES.length;i++){
            COLORMAP3D.put(COLORNAMES[i], COLOR3FARRAY[i]);
        }
    }
    
    public static NamedColor getColorByName(String name){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        NamedColor namedColor = null;
        for (NamedColor nColor : COLORARRAY) {
            if (name.equals(nColor.getName())) {
                namedColor = nColor;
                break;
            }
        }
        return namedColor;
        // </editor-fold>
    }
    
    public static Color3f getColor3fByName(String name){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Color3f color = null;
        for(int i=0;i<COLORNAMES.length;i++){
            String colorName = COLORNAMES[i];
            if(name.equals(colorName)){
                color = COLOR3FARRAY[i];
                break;
            }
        }
        return color;
        // </editor-fold>
    }
    
    public static String getNameOfColor3f(Color3f color3f){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        String name = null;
        for(int i=0;i<COLOR3FARRAY.length;i++){
            if(COLOR3FARRAY[i] == color3f){
                name = COLORNAMES[i];
                break;
            }
        }
        return name;
        // </editor-fold>
    }
    
}
