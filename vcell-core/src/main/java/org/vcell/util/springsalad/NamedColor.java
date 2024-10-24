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
import java.io.Serializable;

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

}
