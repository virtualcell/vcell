/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.springsalad;

import java.awt.Color;
import java.util.HashMap;


public class Colors {

	// Need 24 named colors
	public final static String REDSTRING = "RED"; 				// 1
	public final static String BLUESTRING = "BLUE";
	public final static String LIMESTRING = "LIME";
	public final static String ORANGESTRING = "ORANGE";
	public final static String CYANSTRING = "CYAN"; 			// 5
	public final static String MAGENTASTRING = "MAGENTA";
	public final static String PINKSTRING = "PINK";
	public final static String YELLOWSTRING = "YELLOW";
	public final static String GRAYSTRING = "GRAY";
	public final static String PURPLESTRING = "PURPLE"; 		//10
	public final static String GREENSTRING = "GREEN";
	public final static String MAROONSTRING = "MAROON";
	public final static String NAVYSTRING = "NAVY";
	public final static String OLIVESTRING = "OLIVE";
	public final static String TEALSTRING = "TEAL"; 			// 15
	public final static String LIMEGREENSTRING = "LIME_GREEN";
	public final static String GOLDSTRING = "GOLD";
	public final static String DARKGREENSTRING = "DARK_GREEN";
	public final static String CRIMSONSTRING = "CRIMSON";
	public final static String DARKVIOLETSTRING = "DARK_VIOLET"; //20
	public final static String VIOLETSTRING = "VIOLET"; 
	public final static String SLATEBLUESTRING = "SLATE_BLUE";
	public final static String LIGHTCYANSTRING = "LIGHT_CYAN";
	public final static String DARKCYANSTRING = "DARK_CYAN";
	public final static String LIGHTGRAYSTRING = "LIGHT_GRAY";	// 25
	public final static String DARKGRAYSTRING = "DARK_GRAY";
	public final static String WHITESTRING = "WHITE";
	public final static String BLACKSTRING = "BLACK";
	
	public final static String [] COLORNAMES = { REDSTRING, BLUESTRING,
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
	
	public final static NamedColor [] COLORARRAY = new NamedColor[] { RED, BLUE,
		LIME, ORANGE, CYAN, MAGENTA, PINK, YELLOW, GRAY, PURPLE, GREEN,
		MAROON, NAVY, OLIVE, TEAL, LIMEGREEN, GOLD, DARKGREEN, CRIMSON,
		DARKVIOLET, VIOLET, SLATEBLUE, LIGHTCYAN, DARKCYAN, LIGHTGRAY,
		DARKGRAY, WHITE, BLACK};


	public static NamedColor getColorByName(String name) {
		NamedColor namedColor = null;
		for (NamedColor nColor : COLORARRAY) {
			if (name.equals(nColor.getName())) {
				namedColor = nColor;
				break;
			}
		}
		return namedColor;
	}

}
