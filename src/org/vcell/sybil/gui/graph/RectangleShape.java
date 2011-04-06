package org.vcell.sybil.gui.graph;

/*   RectangleShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Rectangular shaoe used, for example, for containers or for mouse drags
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;


public abstract class RectangleShape extends Shape {

	public RectangleShape (Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
	}

	@Override
	public void updatePrerequisites(Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(label());
	}
	
	@Override
	public void updateOtherSizes(Graphics2D g) {
		labelPos.x = (size.width/2) - labelSize.width/2;
		labelPos.y = (size.height/2) - labelSize.height/2;		
	}
	
	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		preferedSize.height = labelSize.height + 10;
		preferedSize.width = labelSize.width + 10;
		return preferedSize;
	}

	protected void drawLabel(Graphics2D g2D, int absPosX, int absPosY) {

		if (label() != null && label().length() > 0){
			int textX = absPosX  + Math.max(0, size.width/2 - labelSize.width/2);
			int textY = absPosY + Math.max(0, 5 + labelSize.height);
			if(isSelected()){
				int x = textX - 5;
				int y = textY - labelSize.height + 3;
				int width = labelSize.width + 10;
				int height = labelSize.height;
				Color origColor1 = g2D.getColor();
				g2D.setColor(Color.gray);
				g2D.drawRect(x+1,y+1,width,height);
				g2D.drawRect(x+2,y+2,width,height);
				g2D.setColor(Color.white);
				g2D.fillRect(x,y,width,height);
				g2D.setColor(Color.black);
				g2D.drawRect(x,y,width,height);
				g2D.setColor(origColor1);
			}
			Color origColor = g2D.getColor();
			g2D.setColor(Color.black);
			Font origFont = g2D.getFont();
			g2D.setFont(getLabelFont(g2D));
			g2D.drawString(label(),textX,textY);
			g2D.setColor(origColor);
			g2D.setFont(origFont);
		}


	}

	public Font getLabelFont(Graphics2D g) { return getBoldFont(g); }

	@Override
	protected boolean isInside (Point p) {
		int x = p.x - location.x() + size.width/2;
		int y = p.y - location.y() + size.height/2;
		return x > 0 && x < size.width && y > 0 && y < size.height;
	}

	@Override
	public void paint (Graphics2D g2D) {
		int absPosX = location.x() - size.width/2;
		int absPosY = location.y() - size.height/2;
		g2D.setColor(colorBG());
		if (!bNoFill){ g2D.fillRect(absPosX, absPosY, size.width, size.height); }
		g2D.setColor(colorFG());
		g2D.drawRect(absPosX, absPosY, size.width, size.height);
		drawLabel(g2D, absPosX, absPosY);
	}
	
}