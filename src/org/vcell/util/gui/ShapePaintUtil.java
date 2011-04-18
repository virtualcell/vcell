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
		Font font = fontOld.deriveFont((float) (shape.getHeight()/1.5)).deriveFont(Font.BOLD);
		g.setFont(font);
		g.setColor(color);
		FontRenderContext fontRenderContext = g.getFontRenderContext();
		String text = "L";
		TextLayout textLayout = new TextLayout(text, font, fontRenderContext);
		Rectangle2D textBounds = textLayout.getBounds();
		int xText = (int) (shape.getAbsX() + (shape.getWidth() - textBounds.getWidth())/ 2);
		int yText = (int) (shape.getAbsY() + (shape.getHeight() + textBounds.getHeight())/ 2);
		textLayout.draw(g, xText, yText);
		g.setFont(fontOld);
		g.setColor(colorOld);
	}

}
