/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

/* Methods to support layout of shapes
 * September 2010
 */

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

public class ShapeLayoutUtil {

	public static Dimension getTextSize(Graphics graphics, String text) {
		FontMetrics fontMetrics = graphics.getFontMetrics();
		return new Dimension(fontMetrics.stringWidth(text), fontMetrics
				.getMaxAscent()
				+ fontMetrics.getDescent());
	}

	public static Point placeTextUnder(Graphics graphics,
			Dimension size, int padding, String text) {
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int ascent = fontMetrics.getMaxAscent();
		Dimension textSize = new Dimension(fontMetrics.stringWidth(text),
				ascent + fontMetrics.getDescent());
		return new Point(size.width / 2 - textSize.width / 2, size.height + padding + ascent);
	}

}
