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

import cbit.vcell.graph.GraphConstants;

import java.awt.Color;
import java.io.Serializable;

/*
 * Springsalad-style colors.
 * See also Colors
 * For vcell-style colors see GraphConstants
 */
@SuppressWarnings("serial")
public class NamedColor implements Serializable {

	private final Color color;
	private final String name;
	
	public NamedColor(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return name;
	}

	public Color darker(double factor) {
		return new Color(Math.max((int)(color.getRed()*factor), 0),
				Math.max((int)(color.getGreen()*factor), 0),
				Math.max((int)(color.getBlue() *factor), 0),
				color.getAlpha());
	}

	public static Color darker(Color color, double factor) {
		return new Color(Math.max((int)(color.getRed()*factor), 0),
				Math.max((int)(color.getGreen()*factor), 0),
				Math.max((int)(color.getBlue() *factor), 0),
				color.getAlpha());
	}

	public String getHex() {
		Color c = getColor();
		String hex =  getHex(c);
		return hex;
	}
	public static String getHex(Color c) {
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		return hex;
	}

	public static void main(String[] args) {

		Color red = Color.decode("#FF0000");
		Color red1 = new Color(255, 0, 0, 32);
		Color red2 = new Color(255, 0, 0, 127);
		Color red3 = new Color(255, 0, 0, 255);			// plain red

		// some alpha manipulation
		Color darkred = GraphConstants.darkred;		// #8b0000
		Color darkred1 = new Color(139, 0, 0, 32);
		Color darkred2 = new Color(139, 0, 0, 127);
		Color darkred3 = new Color(139, 0, 0, 255);		// plain darkred

		Color brown = Color.decode("#A52A2A");
		Color brown1 = new Color(165, 42, 42, 32);
		Color brown2 = new Color(165, 42, 42, 127);
		Color brown3 = new Color(165, 42, 42, 255);		// plain brown

	}

}
