/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import cbit.gui.graph.Shape;

public class ShapePaintUtil {
	
	
	public static void paintLinkMark(Graphics2D g, Shape shape, Color color) {
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
		Font font = fontOld.deriveFont((float) (shape.getHeight()/2)).deriveFont(Font.BOLD);
		g.setFont(font);
		g.setColor(color);
		FontRenderContext fontRenderContext = g.getFontRenderContext();
		String text = "L";
		TextLayout textLayout = new TextLayout(text, font, fontRenderContext);
		Rectangle2D textBounds = textLayout.getBounds();
		int xText = (int) (shape.getAbsX() + (shape.getWidth() - textBounds.getWidth())/ 2);
		int yText = (int) (shape.getAbsY() + (shape.getHeight() + textBounds.getHeight())/ 2 + 1);
		textLayout.draw(g, xText, yText);
		g.setFont(fontOld);
		g.setColor(colorOld);
	}
	public static void paintLinkMarkRule(Graphics2D g, Shape shape, Color color) {
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
		Font font = fontOld.deriveFont((float) (shape.getHeight()/2)).deriveFont(Font.BOLD);
		g.setFont(font);
		g.setColor(color);
		FontRenderContext fontRenderContext = g.getFontRenderContext();
		String text = "L";
		TextLayout textLayout = new TextLayout(text, font, fontRenderContext);
		Rectangle2D textBounds = textLayout.getBounds();
		int xText = (int) (shape.getAbsX() + (shape.getWidth()+4 - textBounds.getWidth())/ 2 - 1);
		int yText = (int) (shape.getAbsY() + (shape.getHeight()-2 + textBounds.getHeight())/ 2 + 1);
		textLayout.draw(g, xText, yText);
		g.setFont(fontOld);
		g.setColor(colorOld);
	}

}
